# rpi-serverless

Using a Raspberry Pi as an edge-device for serverless function execution.

## Setup

Follow these steps to initially setup the device.

### Preparing the OS

The recommended OS for the Raspberry Pi is Ubuntu Server 20.04 LTS. The image can be flashed to a microSD card using the [rpi-imager](https://github.com/raspberrypi/rpi-imager) tool. The tool will automatically download the required OS and flash it directly to a selected storage device. When choosing the OS, Ubuntu can be found in the section *Other general purpose OS*.

Make sure to edit the `network-config` if you are not using an ethernet connection.

Additional resources on how to set up a Raspberry Pi with Ubuntu Server can be found [here](https://ubuntu.com/tutorials/how-to-install-ubuntu-on-your-raspberry-pi#1-overview).

### faasd

Install `faasd` using the following commands:
```bash
git clone https://github.com/openfaas/faasd
cd faasd
./hack/install.sh
```

The default user for OpenFaas is `admin`. The password con be obtained using `sudo cat /var/lib/faasd/secrets/basic-auth-password`.

