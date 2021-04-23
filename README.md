# rpi-serverless

Using a Raspberry Pi as an edge-device for serverless function execution.

## Quick Start

- Setup the Raspberry Pi with either OpenFaaS or OpenWhisk (Guide can be found in the [Wiki](github.com/doetlingerlukas/rpi-serverless/wiki/Setup-Raspberry-Pi))

## Deploy Function

### OpenFaaS CLI

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

### Using prebuilt functions

The images have already been built and published using the provided `.yml` file. These functions can be used without any additional configuration.

### Deploying a function

The function can be deployed using the rake task
```sh
rake deploy[<function>, <IP or URL>]
```
or a by using `faas-cli` directly
```sh
cd ./functions
faas-cli deploy -f <function>.yml --gateway http://<IP or URL>:8080
```

The function can be invoked using the `faas-cli`:
```sh
echo "<request>" | faas-cli invoke addition --gateway http://<IP or URL>:8080
```

### Building a function

Functions can also be compiled and published. Therefore an up-to-date installation of Docker is required.

Those functions need to be cross-compiled for ARM based devices like the Raspberry Pi. After doing so, we can deploy the function to the device. The compiled function is pushed to a docker registry after successful compilation. Make sure to set the correct prefix for the image in `<function>.yml`, otherwise pushing to the registry fails.

Initially, we need to setup the utilities to do cross-compilation:
```sh
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
```

Afterwards we can publish a function using the rake task
```sh
rake publish[<function>]
```
or a by using `faas-cli` directly
```sh
cd ./functions
faas-cli publish -f <function>.yml --platforms linux/arm64
```
