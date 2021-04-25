# rpi-serverless

Using a Raspberry Pi as an edge-device for serverless function execution.

## Quick Start

- Setup the Raspberry Pi with either OpenFaaS or OpenWhisk (Guide can be found in the [Wiki](github.com/doetlingerlukas/rpi-serverless/wiki/Setup-Raspberry-Pi))
  - See next section on how to install the OpenFaaS CLI to deploy functions
- [Deploy one of the provided functions](github.com/doetlingerlukas/rpi-serverless/wiki/Deploy-a-Function)

## Install OpenFaaS CLI

Install `faas-cli` to connect to the Raspberry Pi.

**Windows:**
```pwsh
scoop install faas-cli
```

**Linux and Mac OS:**
```sh
brew install faas-cli
```
or
```sh
curl -sSL https://cli.openfaas.com | sudo -E sh
```

Connect to the OpenFaaS server using `faas-cli login`. Every function has a dedicated `.yml` file located in `functions`.

The provided `Rakefile` can be used to connect to the gateway using `rake login[<IP or URL>]`. The command requires the OpenFaaS password for the admin user to be saved in `faas-key.txt`. Alternatively a custom key file can be specified using `rake login[<IP or URL>, <path to file>]`.
