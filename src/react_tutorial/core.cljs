(ns react-tutorial.core
    (:require [reagent.core :as reagent]))

;; -------------------------
;; Views

(defn square [& {:keys [value on-click]}]
  [:button.square {:on-click on-click}
   value])

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

(defn board [& {:keys [squares on-click]}]
  (letfn [(render-square [i]
            [square
             :value (squares i)
             :on-click #(on-click i)])]
    [:div
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
      (render-square 8)]]))

(defn game []
  (let [state (reagent/atom {:history [{:squares (vec (repeat 9 nil))}]
                             :x-is-next? true
                             :step-number 0})]
    (letfn [(handle-click [i]
              (let [{:keys [history x-is-next? step-number]} @state
                    history (vec (take (inc step-number) history))
                    current (nth history (dec (count history)))
                    squares (:squares current)]
                (when-not (or (calculate-winner squares)
                              (squares i))
                  (swap! state assoc
                         :history (conj history
                                        {:squares (assoc
                                                   squares
                                                   i
                                                   (if x-is-next? "X" "O"))})
                         :x-is-next? (not x-is-next?)
                         :step-number (count history)))))
            (jump-to [step]
              (swap! state assoc
                     :x-is-next? (even? step)
                     :step-number step))]
      (fn []
        (let [history (:history @state)
              current (nth history (:step-number @state))
              winner (calculate-winner (:squares current))
              status (if winner
                       (str "Winner: " winner)
                       (str "Next player: "
                            (if (:x-is-next? @state) "X" "O")))
              moves (map-indexed (fn [move _]
                                   (let [desc (if (zero? move)
                                                (str "Game start")
                                                (str "Move #" move))]
                                     [:li {:key move}
                                      [:a {:href "#"
                                           :on-click #(jump-to move)}
                                       desc]]))
                                 history)]
          [:div.game
           [:div.game-board
            [board
             :squares (:squares current)
             :on-click handle-click]]
           [:div.game-info
            [:div
             status]
            [:ol
             moves]]])))))

(defn home-page []
  [game])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
