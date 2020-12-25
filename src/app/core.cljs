(ns app.core
  "This namespace contains your application and is the entrypoint for 'yarn start'."
  (:require
   [reagent.core :as reagent]))

(defn square [& {:keys [value on-click]}]
  [:button.square {:on-click on-click}
   value])

(defn board []
  (let [state (reagent/atom {:squares (vec (repeat 9 nil))
                             :x-is-next? true})]
    (letfn [(handle-click [i]
              (let [{:keys [x-is-next?]} @state]
                (swap! state #(-> %
                                  (assoc-in [:squares i]
                                            (if x-is-next? "X" "O"))
                                  (assoc :x-is-next?
                                         (not x-is-next?))))))
            (render-square [i]
              [square
               :value (get-in @state [:squares i])
               :on-click #(handle-click i)])]
      (fn []
        (let [status (str "Next player: "
                          (if (:x-is-next? @state) "X" "O"))]
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
