(ns react-tutorial.core
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

;; -------------------------
;; Views

(defn square [& {:keys [value on-click win?]}]
  [:button.square {:on-click on-click
                   :class (when win?
                            "win-square")}
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
    (reduce (fn [_ [a b c :as win-ps]]
              (when (and (squares a)
                         (= (squares a) (squares b))
                         (= (squares a) (squares c)))
                (reduced [(squares a) win-ps])))
            nil
            lines)))

(defn board [& {:keys [squares on-click win-ps]}]
  (letfn [(render-square [i]
            [square
             :value (squares i)
             :on-click #(on-click i)
             :win? (some #(= i %) win-ps)])]
    (into [:div]
          (->> (range 9)
               (map #(render-square %))
               (partition 3)
               (map #(into [:div.board-row] %))))))

(defn game []
  (let [state (reagent/atom {:history [{:squares (vec (repeat 9 nil))
                                        :i nil}]
                             :x-is-next? true
                             :step-number 0
                             :order-asc? true})]
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
                                                   (if x-is-next? "X" "O"))
                                         :i i})
                         :x-is-next? (not x-is-next?)
                         :step-number (count history)))))
            (jump-to [step]
              (swap! state assoc
                     :x-is-next? (even? step)
                     :step-number step))
            (calculate-location [i]
              (map (comp inc Math/floor) [(/ i 3) (mod i 3)]))
            (flip-sort-order [order-asc?]
              (swap! state assoc
                     :order-asc? (not order-asc?)))]
      (fn []
        (let [history (:history @state)
              selected (:step-number @state)
              current (nth history selected)
              [winner win-ps] (calculate-winner (:squares current))
              status (if winner
                       (str "Winner: " winner)
                       (str "Next player: "
                            (if (:x-is-next? @state) "X" "O")))
              order-asc? (:order-asc? @state)
              sort (fn []
                     [:a {:href "#"
                          :on-click #(flip-sort-order order-asc?)}
                      (if order-asc? "↓" "↑")])
              moves (map-indexed (fn [move {:keys [i]}]
                                   (let [desc (if (zero? move)
                                                (str "Game start")
                                                (str "Move #("
                                                     (str/join
                                                      ", "
                                                      (calculate-location i))
                                                     ")"))]
                                     [:li {:key move
                                           :class (when (= move selected)
                                                    "move-selected")}
                                      [:a {:href "#"
                                           :on-click #(jump-to move)}
                                       desc]]))
                                 history)]
          [:div.game
           [:div.game-board
            [board
             :squares (:squares current)
             :on-click handle-click
             :win-ps win-ps]]
           [:div.game-info
            [:div
             status]
            [:div
             [sort]]
            [:ol
             (if order-asc?
               moves
               (reverse moves))]]])))))

(defn home-page []
  [game])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
