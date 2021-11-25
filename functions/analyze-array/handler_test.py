from .handler import handle
import json

def test_handle():
  input_str = json.dumps({
    "body": list(range(1, 100))
  })
  result = handle(input_str, None)

  assert result['statusCode'] == 200
  assert result['body']['median'] == 50
