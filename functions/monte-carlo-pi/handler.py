import json
import time
import random as r
import math as m

def calc_pi(samples = 10000000):
  inside = 0

  for i in range(0, samples):
    x2 = r.random()**2
    y2 = r.random()**2
    if m.sqrt(x2 + y2) < 1.0:
      inside += 1

  return (float(inside) / samples) * 4

def handle(req):
  start = time.perf_counter()
  req_json = json.loads(req)

  result = calc_pi() if 'benchmark' in req_json else calc_pi(int(req_json['samples']))

  if 'benchmark' not in req_json:
    time.sleep(int(req_json['wait']) / 1000)

  return json.dumps({
    "result": result,
    "runtime": time.perf_counter() - start
  })
