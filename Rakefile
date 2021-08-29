# frozen_string_literal: true

require 'pathname'
require 'open3'
require 'English'

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

desc 'pull OpenFaaS templates'
task :templates do
  system 'faas-cli', 'version'
  raise 'faas-cli not available' unless $CHILD_STATUS.success?

  Dir.chdir('functions') do
    sh 'faas-cli', 'template', 'pull'
    sh 'faas-cli', 'template', 'store', 'pull', 'python3-flask'
  end
end

desc 'build a function file'
task :build, [:f] => :templates do |f: '*'|
  Dir.chdir('functions') do
    Pathname.glob("#{f}.y*ml").each do |file|
      sh 'faas-cli', 'build', '-f', file.to_s
    end
  end
end

desc 'publish function file to docker registry'
task :publish, [:f] do |f: '*'|
  Dir.chdir('functions') do
    Pathname.glob("#{f}.y*ml").each do |file|
      sh 'faas-cli', 'publish', '-f', file.to_s, '--platforms', 'linux/arm64,linux/arm,linux/amd64'
    end
  end
end

desc 'deploy function file to host'
task :deploy, [:f] do |f: '*'|
  Dir.chdir('functions') do
    Pathname.glob("#{f}.y*ml").each do |file|
      sh 'faas-cli', 'deploy', '-f', file.to_s, '--gateway', "http://#{RPI}:8080"
    end
  end
end

task :setup => [:setup_openfaas, :setup_watchdog]

task :connect => [:update_key, :login]
