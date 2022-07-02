//
// Created by Виталий Хлудеев on 18.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class NavigationController : UINavigationController {

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationBar.barStyle = .black
        navigationBar.barTintColor = UIColor(red: 31/255, green: 31/255, blue: 31/255, alpha: 1)
        navigationBar.isTranslucent = false
        navigationBar.tintColor = UIColor(red: 207/255, green: 167/255, blue: 136/255, alpha: 1)
        
        let backImg = UIImage(named: "assets/images/navigation/Backward.png")
        self.navigationBar.backIndicatorImage = backImg
        self.navigationBar.backIndicatorTransitionMaskImage = backImg
    }
}
