(ns ventas.pages.admin
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [bidi.bidi :as bidi]
            [re-frame-datatable.core :as dt]
            [ventas.page :refer [pages]]
            [ventas.routes :as routes]
            [ventas.util :refer [dispatch-page-event]]
            [ventas.i18n :refer [i18n]]))

(defn menu []
  [:ul
   [:li [:a {:href (routes/path-for :admin.users)} "Users"]]
   [:li [:a {:href (routes/path-for :admin.products)} "Products"]]
   [:li [:a {:href (routes/path-for :admin.plugins)} "Plugins"]]])

(defn skeleton [content]
  [:div.admin__skeleton
   [:div.admin__sidebar
    [:a {:href (routes/path-for :admin)}
     [:h3 "Administration"]]
    [menu]]
   [:div.admin__content
    content]])

(defn page []
  [skeleton
   [:p.admin__default-content "Nothing here"]])

(routes/define-route!
 :admin
 {:name (i18n ::page)
  :url "admin"
  :component page})