# BUILD_SIGNED_RELEASE.ps1 - run from the launcher folder
# Requires JDK 17+, Android SDK Build Tools 34.0.0 and a persistent release keystore.
param(
    [string]$SdkRoot = 'E:\Android',
    [string]$KeystorePath = '',
    [string]$Alias = 'tvlauncher'
)
$ErrorActionPreference = 'Stop'
if (-not $KeystorePath) { throw 'Specify -KeystorePath path to a private, backed-up release keystore (do not put it in Git).' }
if (-not (Test-Path $KeystorePath)) { throw "Keystore not found: $KeystorePath" }
$zipalign = Join-Path $SdkRoot 'build-tools\34.0.0\zipalign.exe'
$signer = Join-Path $SdkRoot 'build-tools\34.0.0\apksigner.bat'
if (-not (Test-Path $zipalign) -or -not (Test-Path $signer)) { throw 'Build-tools 34.0.0 missing' }
if (-not (Test-Path '.\gradlew.bat')) { throw 'Open PowerShell in the launcher folder.' }
$env:ANDROID_HOME = $SdkRoot
"sdk.dir=$($SdkRoot.Replace('\','/'))" | Set-Content '.\local.properties' -Encoding ASCII
& .\gradlew.bat assembleRelease --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Gradle release build failed' }
$inputApk = '.\app\build\outputs\apk\release\tv-launcher.apk'
if (-not (Test-Path $inputApk)) { throw "APK not found: $inputApk" }
$outdir = Join-Path (Get-Location).Path 'release-dist'
New-Item -ItemType Directory -Force -Path $outdir | Out-Null
$aligned = Join-Path $outdir 'tv-launcher-aligned-unsigned.apk'
$signed = Join-Path $outdir 'tv-launcher.apk'
& $zipalign -f -p 4 $inputApk $aligned
if ($LASTEXITCODE -ne 0) { throw 'zipalign failed' }
# Read securely, pass using ephemeral environment variables (rather than command-line literals).
$ksPass = Read-Host 'Keystore password' -AsSecureString
$keyPass = Read-Host 'Key password (press Enter only if same is accepted by your environment)' -AsSecureString
function ToPlain([Security.SecureString]$s) {
    $p = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($s)
    try { return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($p) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($p) }
}
try {
    $env:TV_LAUNCHER_KS_PASS = ToPlain $ksPass
    $env:TV_LAUNCHER_KEY_PASS = ToPlain $keyPass
    & $signer sign --ks $KeystorePath --ks-key-alias $Alias --ks-pass env:TV_LAUNCHER_KS_PASS --key-pass env:TV_LAUNCHER_KEY_PASS --out $signed $aligned
    if ($LASTEXITCODE -ne 0) { throw 'APK signing failed' }
    & $signer verify --verbose $signed
    if ($LASTEXITCODE -ne 0) { throw 'APK signature verification failed' }
    Write-Host "Signed APK: $signed"
}
finally {
    Remove-Item Env:\TV_LAUNCHER_KS_PASS -ErrorAction SilentlyContinue
    Remove-Item Env:\TV_LAUNCHER_KEY_PASS -ErrorAction SilentlyContinue
}
