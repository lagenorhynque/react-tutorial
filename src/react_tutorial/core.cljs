(ns react-tutorial.core
    (:require [reagent.core :as reagent]))

;; -------------------------
;; Views

(defn square []
  [:button.square
   ;; TODO
   ])

(defn board []
  (letfn [(render-square [i]
            [square])]
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

(defn home-page []
  [game])

(defn calculate-winner [squares]
  (let [lines [[0 1 2]
               [3 4 5]
               [6 7 8]
               [0 3 6]
               [1 4 7]
               [2 5 8]
               [0 4 8]
               [2 4 6]]]
    (reduce (fn [_ [a b c]]
              (when (and (squares a)
                         (= (squares a) (squares b))
                         (= (squares a) (squares c)))
                (reduced (squares a))))
            nil
            lines)))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
