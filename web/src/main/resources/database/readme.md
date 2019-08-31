# MySQL 性能测试

## 表结构

```mysql
CREATE TABLE `product` (
  `id` varchar(32) NOT NULL DEFAULT '',
  `price` int(11) NOT NULL DEFAULT '0',
  `detail` varchar(1000) NOT NULL DEFAULT '',
  `count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

BTree 索引

## 生成测试数据

```python
import uuid, random, string, sys

def random_str():
    # # length = random.randint(200, 800)
    # length = 300
    # s = []
    # for i in range(length):
    #     s.append(chr(random.randint(0x4e00,0x9fa5)))
    # return "".join(s)
    # 节省时间
    return '辧哗嫬儚皯虪接应罜閿佹収侢秢籨胚缁堵庹梹紃庲囄鞯笳識髃裤領脆螸撉净豛埍簕啫灃鮅瞻犊埣沖涂沩秠輄脚諃呒俙墂聅腢愈婄掹謯玂温踬邜筣犷頾骕鋗祵屰瑙鱤陫夎噼鞆冧愆莾曩蛉沃醔飫葟知静箒嗋嚊穭辴樱殬柸戵牙吤悦懅缣萅祇硿滴膚螼瓬卦皠伖嗔寓禒眼梊虳葦階剬齨煩狞裯鐽饺竇勌雭筛罓娽頃尘闳叿繰褴縧舱揤探蒁爟樥颎睄諐催鲃鏨抰诂菕势滜挄轡騠遜第襔鵲蟏拰裮螯鼠抐廑蝎些藩陗鴜択燾龃嘜浛豮靭卜仐簂蝿飱暫瓞鈆宎騣懩襀錓睰奵鉊巼坷鎼蒦潱魧鲑砺残畀猜彾糘壳病瓣茗嘁殿猆釸淸竚籮渭總縭蔉諧狨垰塮饸襌鮡垯鎂魼現矚頦廰砇艠葹奘誖惥窆睵鬒瀃鄨铣壦胩报鹜鍊痂潸铡鲲蘙刍嬷鄸恏铇鋼肈鶁蓮樈纞麼皲琉躺敀跾磚鹶劻呅僢穋竝豚疩顊宄宩瞮耋滎樗耐蒓乂睱囎籗'

def generate_record():
    key = str(uuid.uuid4()).replace('-', '')
    price = random.randint(1, 100)
    count = 100
    detail = random_str()
    return [key, price, detail, count]

def create_file(num=10000000):
    with open("data.txt", "w") as f:
        for i in range(num):
            if (i+1) % 10000 == 0:
                print(i+1)
            row = generate_record()
            f.write(",".join(map(str, row))+"\n")

if __name__ == '__main__':
    num = int(sys.argv[1])
    import datetime
    start = datetime.datetime.now()
    create_file(num)
    end = datetime.datetime.now()
    cost = (end -start).total_seconds()
    print("generate " + str(num) + " records, Time Used: %s" % cost)
```

使用该脚本 python gen_data.py 10000000

## 导入测试数据

```bash
mysql -uroot -p
>mysql 
load data infile "/var/lib/mysql-files/data.txt" into table product
character set utf8
fields terminated by ','
lines terminated by '\n'
(id, price, detail, count);
```

```bash
Query OK, 10000000 rows affected (2 hours 1 min 27.83 sec)
Records: 10000000  Deleted: 0  Skipped: 0  Warnings: 0
```

```bash
Query OK, 100000000 rows affected (7 hours 13 min 24.65 sec)
Records: 100000000  Deleted: 0  Skipped: 0  Warnings: 0
```

## 测试查询

```mysql
select * from product where id = 4c2a00959c4e4201aae9c50da8a76dac;
```

## MySQL 配置

```bash
# https://www.cnblogs.com/musings/p/5913157.html
###################################################
# https://www.cnblogs.com/mydriverc/p/8296814.html
back_log=512
# 仅 MyISAM https://www.imooc.com/article/47132
key_buffer_size = 512M
# https://blog.csdn.net/hxpjava1/article/details/80522201
max_allowed_packet = 4M
# https://www.imooc.com/article/44501
thread_stack = 256K
# https://www.cnblogs.com/fjping0606/p/6531292.html
table_open_cache = 2048
# https://www.cnblogs.com/paul8339/p/9081915.html
max_connections = 20000
max_user_connection = 15000
# https://www.cnblogs.com/kerrycode/p/8405862.html
max_connect_errors = 10000000
# http://www.manongjc.com/article/8545.html
wait_timeout = 10
# https://www.cnblogs.com/xinysu/p/6439715.html
innodb_thread_concurrency = 0
```

