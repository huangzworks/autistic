(ns autistic.core
    (:require
        [clojurewerkz.neocons.rest :as nr]
        [clojurewerkz.neocons.rest.nodes :as node]
        [clojurewerkz.neocons.rest.relationships :as relationship]
        [clojurewerkz.neocons.rest.cypher :as cypher]
    )
)

;; 连接数据库
;;

(nr/connect! "http://localhost:7474/db/data")


;; 索引的名字
;;

(def user-index "user")
(def follow-index "follow")


;; 添加用户 / 查找用户
;;

(defn add-user!
    "将给定用户添加到数据库中。"
    [user-id]
    (let [
            new-user-node (node/create {:user-id user-id})
         ]
        ; 创建新节点
        ; 并以 "user_id" 为键， user-id 为值，将节点保存到 user-index 索引中
        (node/add-to-index (:id new-user-node) user-index "user_id" user-id)
    )
)

(defn get-user
    "根据给定 id ，在数据库中查找用户。
    没找到则回 nil 。"
    [user-id]
    ; 在索引中查找
    (node/find-one user-index "user_id" user-id)
)


;; 关注 / 取消关注
;;

(defn follow!
    "将 target 添加到 user 的关注当中。"
    [user-id target-id]
    (let [
            user (get-user user-id)
            target (get-user target-id)
         ]
        ; 在两个用户节点之间建立 :follow 关系
        ; 并以 user-id 为键， target-id 为值，将关系添加到 follow-index 索引
        (let [rel (relationship/create user target :follow)]
            (relationship/add-to-index (:id rel) follow-index user-id target-id)
        )
    )
)

(defn unfollow!
    "将 target 从 user 的关注中移除。"
    [user-id target-id]
    (when-let [rel (relationship/find-one follow-index user-id target-id)]
        ; 注意顺序：先删除关系的索引，再删除关系
        (relationship/delete-from-index rel follow-index user-id target-id)
        (relationship/delete rel)
    )
)


;; 返回所有正在关注 / 返回所有关注者
;; 

(defn get-all-following
    "返回所有 user 正在关注的用户的 user-id 。"
    [user-id]
    (let [
            result (cypher/tquery "START user=node:user(user_id = {uid}) 
                                   MATCH user-[:follow]->target 
                                   RETURN target"
                                   {:uid user-id}
                   )
         ]
        (map #(-> (get % "target") :data :user-id) result)
    )
)

(defn get-all-follower
    "返回所有正在关注 user 的用户的 user-id 。"
    [user-id]
    (let [
            result (cypher/tquery "START user=node:user(user_id = {uid})
                                   MATCH follower-[:follow]->user
                                   RETURN follower"
                                   {:uid user-id}
                   )
         ]
        (map #(-> (get % "follower") :data :user-id) result)
    )
)


;; 关系谓词
;;

(defn following?
    "检查 user 是否正在关注 target 。"
    [user-id target-id]
    ; 通过索引，检查两个节点之后是否存在关系
    (let [rel (relationship/find-one follow-index user-id target-id)]
        (not (empty? rel))
    )
)

(defn following-by?
    "检查 user 是否正在被 target 关注。"
    [user-id target-id]
    (following? target-id user-id)
)

(defn following-each-other?
    "检查 user 和 target 是否互相关注了对方。"
    [user-id target-id]
    (and 
        (following? user-id target-id)
        (following? target-id user-id)
    )
)
