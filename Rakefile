# frozen_string_literal: true

require 'pathname'
require 'open3'

RPI = ENV['RPI'] || 'edge-device.local'
HOST = "pi@#{RPI}"

def ssh(*args)
  sh 'ssh', HOST, *args
end

desc 'configure pi boot volume if attached'
task :volume, [:path, :ssid, :psk] do |t, args|
  sh 'touch', "#{args.path}/ssh"

  File.write "#{args.path}/wpa_supplicant.conf", <<~CFG
    country=AT
    ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev
    update_config=1

    network={
        ssid="#{args.ssid}"
        psk="#{args.psk}"
    }
  CFG
end

desc 'configures watchdog on raspberry pi'
task :setup_watchdog do
  ssh <<~SH
    if ! dpkg -s watchdog >/dev/null; then
      sudo apt-get update
      sudo apt-get install -y watchdog
    fi
  SH

  r, w = IO.pipe
  w.puts 'bcm2835_wdt'
  w.close

  ssh 'sudo', 'tee', '/etc/modules-load.d/bcm2835_wdt.conf', in: r

  r, w = IO.pipe
  w.puts <<~CFG
    watchdog-device	= /dev/watchdog
    max-load-1 = 24
  CFG
  w.close

  ssh 'sudo', 'tee', '/etc/watchdog.conf', in: r
  ssh <<~SH
    sudo systemctl enable watchdog
    sudo systemctl start watchdog
  SH
end

desc 'setup faasd on a raspberry pi'
task :setup_openfaas do
  ssh <<~SH
    git clone https://github.com/openfaas/faasd
    cd faasd
    ./hack/install.sh
  SH
end

desc 'retrieve faasd key from host'
task :update_key do
  out, err, status = Open3.capture3 'ssh', HOST, *<<~SH
    sudo cat /var/lib/faasd/secrets/basic-auth-password
  SH

  File.write 'faas-key', out
end

desc 'connect faas-cli to host'
task :login do
  desc 'login to an OpenFaaS gateway'
  sh 'faas-cli', 'login', '-u', 'admin', '-s', '--gateway', "http://#{RPI}:8080", :in=>'faas-key'
end

desc 'publish function to docker registry'
task :publish, [:function] do |function: '*'|
  desc 'publish a function'
  Dir.chdir('functions') do
    Pathname.glob("#{function}.yml").each do |f|
      sh 'faas-cli', 'publish', '-f', f.to_s, '--platforms', 'linux/arm64,linux/arm,linux/amd64'
    end
  end
end

desc 'deploy function to host'
task :deploy, [:function] do |function: '*'|
  desc 'deploy a function to a gateway'
  Dir.chdir('functions') do
    Pathname.glob("#{function}.yml").each do |f|
      sh 'faas-cli', 'deploy', '-f', f.to_s, '--gateway', "http://#{RPI}:8080"
    end
  end
end

task :setup => [:setup_openfaas, :setup_watchdog]

task :connect => [:update_key, :login]
