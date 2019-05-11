(ns ^:figwheel-hooks react-tutorial.core
  (:require [goog.dom :as gdom]
            [reagent.core :as reagent]))

(defn square [& {:keys [value]}]
  [:button.square
   value])

(defn board []
  (letfn [(render-square [i]
            [square :value i])]
    (let [status "Next player: X"]
      [:div
       [:div.status status]
       [:div.board-row
        (render-square 0)
        (render-square 1)
        (render-square 2)]
       [:div.board-row
        (render-square 3)
        (render-square 4)
        (render-square 5)]
       [:div.board-row
        (render-square 6)
        (render-square 7)
        (render-square 8)]])))

(defn game []
  [:div.game
   [:div.game-board
    [board]]
   [:div.game-info
    [:div
     ;; status
     ]
    [:ol
     ;; TODO
     ]]])

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (reagent/render-component [game] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
