from .handler import handle
import json

def test_handle():
  input_str = json.dumps(list(range(1, 100)))
  result, status = handle(input_str)

  assert status == 200
  assert result['median'] == 50

  input_str = json.dumps(list([0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]))
  result, status = handle(input_str)

  assert status == 200
  assert result['median'] == 0.55
