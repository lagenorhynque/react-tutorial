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

(defn board []
  (let [state (reagent/atom {:squares (vec (repeat 9 nil))
                             :x-is-next? true})]
    (letfn [(handle-click [i]
              (let [{:keys [squares x-is-next?]} @state]
                (when-not (or (calculate-winner squares)
                              (squares i))
                  (swap! state #(-> %
                                    (assoc-in [:squares i]
                                              (if x-is-next? "X" "O"))
                                    (assoc :x-is-next?
                                           (not x-is-next?)))))))
            (render-square [i]
              [square
               :value (get-in @state [:squares i])
               :on-click #(handle-click i)])]
      (fn []
        (let [winner (calculate-winner (:squares @state))
              status (if winner
                       (str "Winner: " winner)
                       (str "Next player: "
                            (if (:x-is-next? @state) "X" "O")))]
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
            (render-square 8)]])))))

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

(defn ^:dev/after-load render
  "Render the toplevel component for this app."
  []
  (reagent/render [game] (.getElementById js/document "app")))

(defn ^:export main
  "Run application startup logic."
  []
  (render))
