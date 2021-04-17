import json
import handler

def test_benchmark():
  req = json.dumps({
    'benchmark': True
  })

  res = json.loads(handler.handle(req))
  pi = float(res['result'])
  runtime = float(res['runtime'])

  print(f'PI: {pi} (Calculation took {round(runtime, 2)}s)')
  assert pi > 3.0
  assert pi < 3.5

if __name__ == "__main__":
    test_benchmark()
