# rpi-serverless

Using a Raspberry Pi as an edge-device for serverless function execution.

## Setup Raspberry Pi

Follow these steps to initially setup the device.

### Preparing the OS

The recommended OS for the Raspberry Pi is Ubuntu Server 20.04 LTS. The image can be flashed to a microSD card using the [rpi-imager](https://github.com/raspberrypi/rpi-imager) tool. The tool will automatically download the required OS and flash it directly to a selected storage device. When choosing the OS, Ubuntu can be found in the section *Other general purpose OS*.

Make sure to edit the `network-config` if you are not using an ethernet connection.

Additional resources on how to set up a Raspberry Pi with Ubuntu Server can be found [here](https://ubuntu.com/tutorials/how-to-install-ubuntu-on-your-raspberry-pi#1-overview).

### Install OpenFaaS

Install `faasd` using the following commands:
```bash
git clone https://github.com/openfaas/faasd
cd faasd
./hack/install.sh
```

The default user for OpenFaaS is `admin`. The password con be obtained using `sudo cat /var/lib/faasd/secrets/basic-auth-password`.

OpenFaaS uses `containerd`. Make sure that there is no installation of Docker on the device. If you run into any problems, check if the system is up to date. Verify that `runc` is installed, as the `faasd` service won't run without it.


## Prerequisites

- up-to-date installation of Docker

## OpenFaaS

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

### Building a function

Those functions need to be cross-compiled for ARM based devices like the Raspberry Pi. After doing so, we can deploy the function to the device. The compiled function is pushed to a docker registry after successful compilation. Make sure to set the correct prefix for the image in `<function>.yml`, otherwise pushing to the registry fails.

Initially, we need to setup the utilities to do cross-compilation:
```sh
docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
```

Afterwards we can publish a function using the following command:
```sh
faas-cli publish -f function.yml --platforms linux/arm64
```

### Using prebuilt functions

The images have already been built and published using the provided `.yml` file.

### Deploying a function

The function can be deployed using the following command:
```sh
faas-cli deploy -f <function>.yml --gateway http://<IP or URL>:8080
```

The function can be invoked using the `faas-cli`:
```sh
echo "<request>" | faas-cli invoke addition --gateway http://<IP or URL>:8080
```