param(
    [string]$RegistryPrefix = "ea-management",
    [string]$Tag = "1.0",
    [string]$ContainerCli = "docker"
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$modules = @(
    "eureka-server",
    "employee-service",
    "attendance-service",
    "leave-service",
    "gateway-service"
)

function Get-ImageName {
    param([string]$ModuleName)

    if ([string]::IsNullOrWhiteSpace($RegistryPrefix)) {
        return "$ModuleName`:$Tag"
    }

    return "$RegistryPrefix/$ModuleName`:$Tag"
}

Push-Location $root
try {
    mvn clean package -DskipTests

    foreach ($module in $modules) {
        $imageName = Get-ImageName -ModuleName $module
        & $ContainerCli build -t $imageName $module
    }
}
finally {
    Pop-Location
}
