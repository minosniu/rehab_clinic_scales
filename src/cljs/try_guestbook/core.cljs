(ns try-guestbook.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [try-guestbook.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]
            )
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:ul.nav.navbar-nav>a.navbar-brand
   {:class    (when (= page (session/get :page)) "active")
    :href     uri
    :on-click #(reset! collapsed? true)}
   title])

(defn nav-bar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-light.bg-faded
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "try-guestbook"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "首页" :home collapsed?]
         [nav-link "#/about" "关于我们" :about collapsed?]]]])))

(def g-query-results (r/atom "foobar"))
(def g-query-string (r/atom "foobar"))

;(defn query-input [value]
;  [:div.container
;   [:input {:type      "text"
;            :value     @value
;            :on-change (fn [e]
;                         (reset! value (-> e .-target .-value))
;                         )}]])

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     "this is the story of try-guestbook... work in progress"]]])

(defn es-n-reset [query-atom]
  (GET (str "/elasticsearch?" @g-query-string)
       {:handler
        #(reset! query-atom %)})
  )

(defn query-input [value]
  [:div.container
   [:input {:type      "text"
            :value     @value
            :on-change (fn [e]
                         (reset! value (-> e .-target .-value))
                         (es-n-reset g-query-results)
                         )}]])

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "康复在瑞金: 临床量表查询"]
    [:p "Time to start building your site!"]
    [:p
     [:a.btn.btn-primary.btn-lg
      {:on-click
       #(es-n-reset g-query-results)} "查询 »"]]]
   [:div.container
    [:p "康复在瑞金: 临床量表查询"]
    [query-input g-query-string]
    [:p "查询结果:"]
    [:p @g-query-results]
    ]
   ])

(def pages
  {:home  #'home-page
   :about #'about-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :page :home))

(secretary/defroute "/about" []
                    (session/put! :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)}))

(defn mount-components []
  (r/render [#'nav-bar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
