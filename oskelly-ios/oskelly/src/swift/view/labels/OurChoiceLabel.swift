//
// Created by Виталий Хлудеев on 16.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class OurChoiceLabel : UILabel {

    init() {
        super.init(frame: CGRect())
        self.text = "Наш выбор"
        self.textColor = .white
        self.backgroundColor = UIColor(red: 206/255, green: 167/255, blue: 138/255, alpha: 1)
        self.textAlignment = .center
        self.font = BoldFont.systemFont(ofSize: 7.0)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
