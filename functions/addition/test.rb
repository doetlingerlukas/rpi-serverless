# frozen_string_literal: true

require 'json'
require 'rspec/autorun'
require_relative 'handler'

describe Handler do
  it "run benchmark" do
    handler = Handler.new

    req = { 'benchmark' => true }.to_json
    res = JSON.parse(handler.run(req))

    expect(res['sum']).to be_between(2, 20).inclusive
  end

  it "add two numbers" do
    handler = Handler.new

    req = {
      'firstSummand' => 100,
      'secondSummand' => 100
    }.to_json
    res = JSON.parse(handler.run(req))

    expect(res['sum']).to eq(200)
  end
end
