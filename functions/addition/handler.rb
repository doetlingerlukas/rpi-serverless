require 'json'

class Handler
  def run(req)
    req_json = JSON.parse(req)

    sleep(req_json['waitTimeIn'].to_i / 1000)

    result = { 'sum' => req_json['firstSummand'].to_i + req_json['secondSummand'].to_i }
    return result.to_json
  end
end
