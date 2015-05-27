(ns ^:figwheel-always adapter.core
    (:require [chord.client :refer [ws-ch]]
              [cljs.core.async :refer [chan <! >! put! close! timeout]])
    (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(def sock-url "ws://localhost:8088")
(def auth-obj (js-obj "action" "request-authorization"
                      "payload" (js-obj "type" "application-token"
                                        "authorizationToken" "")))

(set! (.-onload js/window)
       (fn []
         (go
           (let [{:keys [ws-channel error]} (<! (ws-ch sock-url {:format :json}))] 
             (if error
               (js/console.log "no way Jose")
               (go 
                 (>! ws-channel auth-obj ))
               )))))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
) 

