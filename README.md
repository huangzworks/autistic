# Autistic

Autistic 是一个用 Neo4j 实现基本社交关系的库，功能包括：

    * 用户
        * 添加用户 ``add-user!``
        * 删除用户 ``remove-user!``
        * 获取用户 ``get-user!
    * 关注
        * 添加关注 ``follow!``
        * 取消关注 ``unfollow!``
        * 获取关注关系 ``get-following-relationship``
        * 返回所有正在关注的人 ``get-all-follower``
        * 返回正在关注的人的数量 ``count-follower-number``
        * 返回所有关注者 ``get-all-follower``
        * 返回关注者的数量 ``count-follower-number``
    * 关系谓词
        * 正在关注？ ``following?``
        * 正在被关注？ ``following-by?``
        * 两个人互相关注？ ``following-each-other?``


## 用例

### 添加/删除/查找用户

    user=> (use 'autistic.core)
    nil

    user=> (add-user! "huangz")
    #clojurewerkz.neocons.rest.records.Node{:id 1965, :location-uri "http://localhost:7474/db/data/node/1965", :data {:uid "huangz"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/1965/relationships"}

    user=> (get-user "huangz")
    #clojurewerkz.neocons.rest.records.Node{:id 1965, :location-uri "http://localhost:7474/db/data/node/1965", :data {:uid "huangz"}, :relationships-uri "http://localhost:7474/db/data/node/1965/relationships/all", :create-relationship-uri "http://localhost:7474/db/data/node/1965/relationships"}

    user=> (remove-user! "huangz")
    [1965 204]

    user=> (get-user "huangz")
    nil

### 关注/取消关注/获取关注关系

    user=> (add-user! "huangz")
    #clojurewerkz.neocons.rest.records.Node{:id 2014, :location-uri "http://localhost:7474/db/data/node/2014", :data {:uid "huangz"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/2014/relationships"}

    user=> (add-user! "peter")
    #clojurewerkz.neocons.rest.records.Node{:id 2015, :location-uri "http://localhost:7474/db/data/node/2015", :data {:uid "peter"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/2015/relationships"}

    user=> (get-following-relationship "huangz" "peter")
    nil

    user=> (follow! "huangz" "peter")
    #clojurewerkz.neocons.rest.records.Relationship{:id 762, :location-uri "http://localhost:7474/db/data/relationship/762", :start "http://localhost:7474/db/data/node/2014", :end "http://localhost:7474/db/data/node/2015", :type "follow", :data {}}

    user=> (get-following-relationship "huangz" "peter")
    #clojurewerkz.neocons.rest.records.Relationship{:id 762, :location-uri "http://localhost:7474/db/data/relationship/762", :start "http://localhost:7474/db/data/node/2014", :end "http://localhost:7474/db/data/node/2015", :type "follow", :data {}}

    user=> (unfollow! "huangz" "peter")
    [762 204]

    user=> (get-following-relationship "huangz" "peter")
    nil

### 正在关注？/正在被关注？正在互相关注？

    user=> (add-user! "huangz")
    #clojurewerkz.neocons.rest.records.Node{:id 2016, :location-uri "http://localhost:7474/db/data/node/2016", :data {:uid "huangz"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/2016/relationships"}

    user=> (add-user! "peter")
    #clojurewerkz.neocons.rest.records.Node{:id 2017, :location-uri "http://localhost:7474/db/data/node/2017", :data {:uid "peter"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/2017/relationships"}

    user=> (following? "huangz" "peter")
    false

    user=> (follow! "huangz" "peter")
    #clojurewerkz.neocons.rest.records.Relationship{:id 763, :location-uri "http://localhost:7474/db/data/relationship/763", :start "http://localhost:7474/db/data/node/2016", :end "http://localhost:7474/db/data/node/2017", :type "follow", :data {}}

    user=> (following? "huangz" "peter")
    true

    user=> (following-by? "huangz" "peter")
    false

    user=> (follow! "peter" "huangz")
    #clojurewerkz.neocons.rest.records.Relationship{:id 764, :location-uri "http://localhost:7474/db/data/relationship/764", :start "http://localhost:7474/db/data/node/2017", :end "http://localhost:7474/db/data/node/2016", :type "follow", :data {}}

    user=> (following-by? "huangz" "peter")
    true

    user=> (following-each-other? "huangz" "peter")
    true

### 返回所有关注者（的数量）/返回所有正在关注的人（的数量）

    user=> (add-user! "huangz")
    #clojurewerkz.neocons.rest.records.Node{:id 2064, :location-uri "http://localhost:7474/db/data/node/2064", :data {:uid "huangz"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/2064/relationships"}

    user=> (add-user! "peter")
    #clojurewerkz.neocons.rest.records.Node{:id 2065, :location-uri "http://localhost:7474/db/data/node/2065", :data {:uid "peter"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/2065/relationships"}

    user=> (add-user! "mary")
    #clojurewerkz.neocons.rest.records.Node{:id 2066, :location-uri "http://localhost:7474/db/data/node/2066", :data {:uid "mary"}, :relationships-uri nil, :create-relationship-uri "http://localhost:7474/db/data/node/2066/relationships"}

    user=> (get-all-following "huangz" 0 10)    ; 0 是 skip 的条目数量， 10 是 limit 的条目数量
    ()

    user=> (count-following-number "huangz")
    0

    user=> (follow! "huangz" "peter")
    #clojurewerkz.neocons.rest.records.Relationship{:id 785, :location-uri "http://localhost:7474/db/data/relationship/785", :start "http://localhost:7474/db/data/node/2064", :end "http://localhost:7474/db/data/node/2065", :type "follow", :data {}}

    user=> (get-all-following "huangz" 0 10)
    ("peter")

    user=> (count-following-number "huangz")
    1

    user=> (follow! "huangz" "mary")
    #clojurewerkz.neocons.rest.records.Relationship{:id 786, :location-uri "http://localhost:7474/db/data/relationship/786", :start "http://localhost:7474/db/data/node/2064", :end "http://localhost:7474/db/data/node/2066", :type "follow", :data {}}

    user=> (get-all-following "huangz" 0 10)
    ("peter" "mary")

    user=> (count-following-number "huangz")
    2

    user=> (get-all-follower "huangz" 0 10)
    ()

    user=> (count-follower-number "huangz")
    0

    user=> (follow! "peter" "huangz")
    #clojurewerkz.neocons.rest.records.Relationship{:id 787, :location-uri "http://localhost:7474/db/data/relationship/787", :start "http://localhost:7474/db/data/node/2065", :end "http://localhost:7474/db/data/node/2064", :type "follow", :data {}}

    user=> (get-all-follower "huangz" 0 10)
    ("peter")

    user=> (count-follower-number "huangz")
    1


## 许可

Copyright (C) 2012 huangz(huangz1990@gmail.com)

Distributed under the Eclipse Public License, the same as Clojure.
