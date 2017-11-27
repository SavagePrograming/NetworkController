import random

def distance(x, y, x1, y1):
    return ((x - x1) ** 2 + (y - y1) ** 2) ** .5

def getClosestNode(nodes, node):
    MIN = nodes[0]
    for n in nodes:
        if distance(node[1], node[2], n[1], n[2]) < distance(node[1], node[2], MIN[1], MIN[2]):
            MIN = n
    return MIN

def outOfRange(nodes, node, RANGE):
    for n in nodes:
        if distance(node[1], node[2], n[1], n[2]) <= RANGE:
            return False
    return True

FILEPATH= raw_input("File Name:")
FILE = open(FILEPATH+".nd", "w")
nodes = []
count = 100

while count > 0:
    x = random.randint(20,480)
    y = random.randint(20,480)
    node = ["0" * (3 - len(str(x))) + str(x) + "0" * (3 - len(str(y))) + str(y), x,y]
    if (outOfRange(nodes,node, 20)):
        print "Create " + node[0]
        FILE.write("Create " + node[0] + "\n")
        print "Move " + node[0] + " " + str(x) + " " + str(y)
        FILE.write("Move " + node[0] + " " + str(x) + " " + str(y) + "\n")
        if nodes:
            n2 = getClosestNode(nodes, node)
            print "Connect " + node[0] + " " + n2[0]
            FILE.write("Connect " + node[0] + " " + n2[0] + "\n")
            print "Connect " + n2[0] + " " + node[0]
            FILE.write("Connect " + n2[0] + " " + node[0] + "\n")
        
        nodes.append(node)
        count -= 1

FILE.close()
    
    
