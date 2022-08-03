from pyspark import SparkConf, SparkContext


conf = SparkConf().setMaster("local").setAppName("gradecount")
sc = SparkContext(conf = conf)
grades = sc.textFile("/user/grades.txt")
 
def f(x):
    if(x >= 90 and x <= 100):
        return ("90-100")
    if(x >= 80 and x < 90):
        return ("80-89") 
    if(x >= 70 and x < 80):
        return ("70-79") 
    if(x >= 60 and x < 70):
        return ("60-69") 
    if(x < 60):
        return ("0-59") 

# 170315班,史伦泰,计算机图形学,选修,82
# 先过滤掉课程性质为选修的行。这里filter的操作对象是文件的一行
grades = grades.filter(lambda line: "必修" in line)

# 将每一行映射为一个(姓名, (成绩, 1))对
name_grade_pairs = grades.map(lambda line: (line.split(",")[1], (int(line.split(",")[4]), 1)))
# name_grade_pairs.saveAsTextFile("name_grade_pairs")

# 合并同key行，求出每个人的成绩总和以及成绩条目总数，然后求出平均成绩
# lambda x, y是对两个同key的value之间进行运算
avg_grades = name_grade_pairs.reduceByKey(lambda x1, x2: (x1[0] + x2[0], x1[1] + x2[1])).mapValues(lambda x: int(x[0] / x[1])).sortBy(lambda x: x[1], ascending=True)
# 保存结果
avg_grades.saveAsTextFile("avg_grades")

# 下面统计各分段人数
# 先将平均成绩计算结果的每一行映射为一个("分段", 1)对
interval_grades = avg_grades.map(lambda x: (f(x[1]), 1))
# 再利用reduceByKey求出各分段的总人数
# lambda x, y是对两个同key的kv对之间进行计算
interval_stu_nums = interval_grades.reduceByKey(lambda x, y: (x + y)).sortByKey()
# 保存结果
interval_stu_nums.saveAsTextFile("interval_stu_nums")

