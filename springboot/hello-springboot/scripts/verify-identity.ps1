# verify-identity.ps1
# Purpose: prove that "who is calling" comes from the JWT, not from a hard-coded value.
# Usage:   powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verify-identity.ps1
# Precondition: the app is running on http://localhost:8080

$ErrorActionPreference = 'Stop'
$Base = 'http://localhost:8080'

function Post-Json($path, $json) {
    $tmp = New-TemporaryFile
    Set-Content -LiteralPath $tmp -Value $json -Encoding ASCII
    $out = curl.exe -s -X POST "$Base$path" -H 'Content-Type: application/json' --data-binary "@$tmp"
    Remove-Item $tmp -Force
    return $out
}

function Get-Token($user, $pass) {
    $body = '{"username":"' + $user + '","password":"' + $pass + '"}'
    $resp = Post-Json '/api/auth/login' $body
    try { $obj = $resp | ConvertFrom-Json } catch { return $null }
    if ($obj.token) { return $obj.token }
    return $null
}

function Ensure-Account($user, $pass) {
    if (Get-Token $user $pass) { return $true }
    $body = '{"username":"' + $user + '","password":"' + $pass + '"}'
    $resp = Post-Json '/api/auth/register' $body
    return [bool](Get-Token $user $pass)
}

function Status-Of($url, $token) {
    if ($token) {
        $code = curl.exe -s -o NUL -w '%{http_code}' $url -H "Authorization: Bearer $token"
    } else {
        $code = curl.exe -s -o NUL -w '%{http_code}' $url
    }
    return $code
}

Write-Host '=== step 0: make sure two different accounts exist ==='
if (-not (Ensure-Account 'test01' '123456')) { throw 'cannot login/register test01' }
if (-not (Ensure-Account 'test02' '123456')) { throw 'cannot login/register test02' }

$tokenA = Get-Token 'test01' '123456'
$tokenB = Get-Token 'test02' '123456'
Write-Host ("tokenA (test01) length = " + $tokenA.Length)
Write-Host ("tokenB (test02) length = " + $tokenB.Length)

Write-Host ''
Write-Host '=== step 1-4: acceptance ==='
$r1 = Status-Of "$Base/api/users" $tokenA
$r2 = Status-Of "$Base/api/users" $null
$r3 = Status-Of "$Base/api/users" 'abc'
$r4 = Status-Of "$Base/api/users" $tokenB

Write-Host ("1) with valid token (test01)   -> expect 200, got " + $r1)
Write-Host ("2) without any header          -> expect 401, got " + $r2)
Write-Host ("3) with fake token 'abc'       -> expect 401, got " + $r3)
Write-Host ("4) with another user (test02)  -> expect 200, got " + $r4)

Write-Host ''
Write-Host '=== what to look at in the IDEA console ==='
Write-Host 'Two lines with DIFFERENT ids must appear, e.g.:'
Write-Host '  当前访问者 id = 8      (test01)'
Write-Host '  当前访问者 id = 9      (test02)'
Write-Host 'Same id twice  => the value is hard-coded or cached (WRONG).'
