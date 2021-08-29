import json
import time

def heat_stencil(N = 50):
    T = N * N

    A = [[273 for x in range(N)] for y in range(N)]
    B = [[273 for x in range(N)] for y in range(N)]

    source_x = int(N / 4)
    source_y = int(N / 4)

    A[source_x][source_y] = 273 + 60

    for t in range(T):
        for i in range(N):
            for j in range(N):

                if i == source_x and j == source_y:
                    B[i][j] = A[i][j]

                t_current = A[i][j]

                t_upper = A[i - 1][j] if i != 0 else t_current
                t_lower = A[i + 1][j] if i != N - 1 else t_current
                t_left = A[i][j - 1] if j != 0 else t_current
                t_right = A[i][j + 1] if j != N - 1 else t_current

                B[i][j] = t_current + 0.2 * (t_left + t_right + t_upper + t_lower + (-4 * t_current))

                # swap matrices
                C = A
                A = B
                B = C

    return A


def handle(req):
    start = time.perf_counter()
    req_json = json.loads(req)

    heat_stencil() if 'benchmark' in req_json else heat_stencil(int(req_json['samples']))

    return json.dumps({
      "result": 1,
      "runtime": time.perf_counter() - start
    })