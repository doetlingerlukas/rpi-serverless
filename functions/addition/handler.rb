# frozen_string_literal: true

require 'json'

class Handler
  def initialize
    @start_time = Process.clock_gettime(Process::CLOCK_MONOTONIC)
  end

  def run(req)
    req_json = JSON.parse(req)
    sum = nil

    unless req_json['benchmark'].nil?
      sum = rand(1..10) + rand(1..10)
    else
      sleep(req_json['waitTimeIn'].to_i / 1000)
      sum = req_json['firstSummand'].to_i + req_json['secondSummand'].to_i
    end

    result = {
      'sum' => sum,
      'runtime' => Process.clock_gettime(Process::CLOCK_MONOTONIC) - @start_time
    }
    return result.to_json
  end
end
