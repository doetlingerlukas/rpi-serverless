import json
import statistics

def verify_input(input_) -> bool:
  if isinstance(input_, list):
    if all(isinstance(e, int) or isinstance(e, float) for e in input_):
      return True

  return False

def handle(event):

  input_array = json.loads(event)

  if not verify_input(input_array):
    return {
      "error": "Bad input supplied, expected array of numbers."
    }, 400

  return {
    "mean": statistics.mean(input_array),
    "median": statistics.median(input_array),
    "median_low": statistics.median_low(input_array),
    "median_high": statistics.median_high(input_array),
    "variance": statistics.variance(input_array),
    "pvariance": statistics.pvariance(input_array),
    "stdev": statistics.stdev(input_array),
    "pstdev": statistics.pstdev(input_array)
  }, 200
