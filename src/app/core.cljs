(ns app.core
  "This namespace contains your application and is the entrypoint for 'yarn start'."
  (:require
   [reagent.core :as reagent]))

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
                             :x-is-next? true})]
    (letfn [(handle-click [i]
              (let [{:keys [history x-is-next?]} @state
                    current (nth history (dec (count history)))
                    squares (:squares current)]
                (when-not (or (calculate-winner squares)
                              (squares i))
                  (swap! state assoc
                         :history (conj history
                                        {:squares
                                         (assoc squares i (if x-is-next? "X" "O"))})
                         :x-is-next? (not x-is-next?)))))]
      (fn []
        (let [{:keys [history x-is-next?]} @state
              current (nth history (dec (count history)))
              winner (calculate-winner (:squares current))
              status (if winner
                       (str "Winner: " winner)
                       (str "Next player: " (if x-is-next? "X" "O")))
              moves (map-indexed (fn [move _]
                                   (let [desc (if (zero? move)
                                                (str "Go to game start")
                                                (str "Go to move #" move))]
                                     [:li
                                      [:button {:on-click #(jump-to move)}
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

(defn ^:dev/after-load render
  "Render the toplevel component for this app."
  []
  (reagent/render [game] (.getElementById js/document "app")))

(defn ^:export main
  "Run application startup logic."
  []
  (render))
