(ns nimaeskandary.user.interface.types)

(def User
  [:map
   [:id :uuid]
   [:username :string]
   [:email :string]])

(def save-user [:=> [:cat :map User] User])
(def get-user [:=> [:cat :map :uuid] User])