(ns ventas.components.notificator
  (:require [ventas.util :as util]
            [soda-ash.core :as sa]
            [clojure.string :as s]
            [cljs.core.async :refer [<! >! put! close! timeout chan]]
            [re-frame.core :as rf])
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)]))

(rf/reg-sub :app/notifications
  (fn [db _] (-> db :notifications)))

(rf/reg-event-db :app/notifications.add
 (fn event-notifications-add [db [_ notification]]
   (let [sym (gensym)
         notification (assoc notification :sym sym)]
     (go
      (<! (timeout 4000))
      (rf/dispatch [:app/notifications.remove sym]))
     (if (vector? (:notifications db))
       (assoc db :notifications (conj (:notifications db) notification))
       (assoc db :notifications [notification])))))

(rf/reg-event-db :app/notifications.remove
  (fn event-notifications-remove [db [_ sym]]
    (assoc db :notifications (remove #(= (:sym %) sym) (:notifications db)))))

(defn notificator []
  "Displays notifications"
  [:div.ventas {:fqcss [::notificator]}
    (for [notification @(rf/subscribe [:app/notifications])]
      [:div {:class (s/join " " ["bu" "notification" (:theme notification)])}
        [sa/Icon {:class "bu close" :name (:icon notification) :on-click #(rf/dispatch [:app/notifications.remove (:sym notification)])}]
        [:p {:class "bu message"} (:message notification)]
      ])])