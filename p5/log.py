def handle_time(path):
    f = open(path, "r")
    times = f.readlines()
    times = [int(x.rstrip()) for x in times]
    avg = sum(times) / len(times)
    return avg / 10**6



print(handle_time("TS_demo3.txt"))

print(handle_time("TJ_demo3.txt"))
