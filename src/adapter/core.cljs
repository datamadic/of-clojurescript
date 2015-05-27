(ns ^:figwheel-always adapter.core
    (:require [chord.client :refer [ws-ch]]
              [cljs.core.async :refer [chan <! >! put! close! take! timeout]]
              [goog.dom :as dom]
              [goog.events :as events])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(def sock-url "ws://localhost:8088")
(def openfin-sock "ws://localhost:9696/")
(def auth-obj (js-obj "action" "request-authorization"
                      "payload" (js-obj "type" "application-token"
                                        "authorizationToken" "B6E84F0C-8045-4257-9C32-61FF4EEA2364")))
(defn auth
  "send the auth message"
  [ch msg]
  (go
    (>! ch msg)))

(defn listen 
  [el type]
  (let [out (chan)]
    (events/listen el type 
                   (fn [e] (put! out e)))
    out))

(set! (.-onload js/window)
       (fn []

         (let [clicks (listen (dom/getElement "connect") "click") 
               typing (listen (dom/getElement "token") "change")]
           (go 
             (while true 
               (js/console.log (<! clicks))))
           (go
             (while true
               (js/console.log (<! typing)))))
                  
         (go
           (let [{:keys [ws-channel error]} (<! (ws-ch openfin-sock {:format :json}))] 
             (if error
               (js/console.log "no way Jose")
               (do
                ; (listen ws-channel)
                 (go
                   (>! ws-channel auth-obj))))))))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
) 



