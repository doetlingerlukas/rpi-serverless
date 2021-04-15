# frozen_string_literal: true

require 'pathname'
require 'open3'

task :login, [:gateway, :key] do |gateway: '127.0.0.1', key: 'faas-key.txt'|
  desc 'login to an OpenFaaS gateway'
  Open3.pipeline ['faas-cli', 'login', '-u', 'admin', '-s', '--gateway', "http://#{gateway}:8080"], :in=>key
end

task :publish, [:function] do |function: '*'|
  desc 'publish a function'
  Dir.chdir('functions') do
    Pathname.glob("#{function}.yml").each do |f|
      sh 'faas-cli', 'publish', '-f', f.to_s, '--platforms', 'linux/arm64'
    end
  end
end

task :deploy, [:function, :gateway] do |function: '*', gateway: '127.0.0.1'|
  desc 'deploy a function to a gateway'
  Dir.chdir('functions') do
    Pathname.glob("#{function}.yml").each do |f|
      sh 'faas-cli', 'deploy', '-f', f.to_s, '--pgateway', "http://#{gateway}:8080"
    end
  end
end
