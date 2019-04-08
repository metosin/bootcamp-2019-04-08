(ns backend.web
  (:require [common.localization :refer [tr]]
            [hiccup.core :as hiccup]
            [hiccup.page :as page]
            [ring.middleware.content-type :as content-type]
            [ring.util.response :as response]))

(defn index []
  (hiccup/html
   (page/html5
    [:head
     [:title (tr :page-title)]
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible"
             :content "IE=edge"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
     (page/include-css "/css/style.css")]
    [:body
     [:div#app
      [:div.loading
       [:h1 (tr :loading)]]]
     [:div#dev]
     (page/include-js "/js/main.js")])))

(defn create-index-handler []
  (fn [req]
    (if (= (:request-method req) :get)
      (-> (index)
        response/response
        (response/content-type "text/html; charset=utf-8")))))

(defn create-static-handler []
  (-> (fn [req]
        (if (= (:request-method req) :get)
          (response/resource-response (:uri req))))
    (content-type/wrap-content-type)))
