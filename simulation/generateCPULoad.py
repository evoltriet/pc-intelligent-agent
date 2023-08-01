from multiprocessing import Pool, cpu_count
from time import sleep

mode = 'cpu'
#mode : mem,cpu

def r(x):
	n = 1
	while True:
		x = x*x
		sleep(1/n)
		n+=(1/(n*n))
		
def cpu():
	procs = cpu_count()
	print('Simulating CPU Usage')
	print('utilizing %d cores\n' % procs)
	pool = Pool(procs)
	pool.map(r, range(procs))

def mem():
	print('Simulating Memory Usage')
	x = 4
	while True:
		x = x*x

if __name__ == '__main__':
	exec(mode + '()')