import json
import statistics

def verify_input(input_) -> bool:
  if isinstance(input_, list):
    if all(isinstance(e, int) for e in input_):
      return True

  return False

def handle(event, context):

  input_array = json.loads(event)['body']

  if not verify_input(input_array):
    return {
      "statusCode": 400,
      "body": {
        "error": "Bad input supplied, expected array of numbers."
      }
    }

  return {
    "statusCode": 200,
    "body": {
      "mean": statistics.mean(input_array),
      "median": statistics.median(input_array),
      "median_low": statistics.median_low(input_array),
      "median_high": statistics.median_high(input_array),
      "variance": statistics.variance(input_array),
      "pvariance": statistics.pvariance(input_array),
      "stdev": statistics.stdev(input_array),
      "pstdev": statistics.pstdev(input_array)
    }
  }
