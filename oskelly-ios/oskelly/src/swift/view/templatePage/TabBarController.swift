//
// Created by Виталий Хлудеев on 18.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class TabBarController : UITabBarController {

    override func viewDidLoad() {
        tabBar.barStyle = .black
        tabBar.barTintColor = UIColor(red: 26/255, green: 26/255, blue: 26/255, alpha: 1)
        tabBar.tintColor = AppColors.lato()
        tabBar.unselectedItemTintColor = UIColor(red: 125/255, green: 124/255, blue: 125/255, alpha: 1)
        tabBar.isTranslucent = false

        tabBar.items?[0].selectedImage = UIImage(named: "assets/images/tabs/Home.png")?.withRenderingMode(.alwaysTemplate)
        tabBar.items?[0].image = UIImage(named: "assets/images/tabs/Home.png")?.withRenderingMode(.alwaysOriginal)
        tabBar.items?[1].selectedImage = UIImage(named: "assets/images/tabs/Search.png")?.withRenderingMode(.alwaysTemplate)
        tabBar.items?[1].image = UIImage(named: "assets/images/tabs/Search.png")?.withRenderingMode(.alwaysOriginal)
        tabBar.items?[2].selectedImage = UIImage(named: "assets/images/tabs/Sell.png")?.withRenderingMode(.alwaysTemplate)
        tabBar.items?[2].image = UIImage(named: "assets/images/tabs/Sell.png")?.withRenderingMode(.alwaysOriginal)
        tabBar.items?[3].selectedImage = UIImage(named: "assets/images/tabs/Alarm.png")?.withRenderingMode(.alwaysTemplate)
        tabBar.items?[3].image = UIImage(named: "assets/images/tabs/Alarm.png")?.withRenderingMode(.alwaysOriginal)
        tabBar.items?[4].selectedImage = UIImage(named: "assets/images/tabs/User.png")?.withRenderingMode(.alwaysTemplate)
        tabBar.items?[4].image = UIImage(named: "assets/images/tabs/User.png")?.withRenderingMode(.alwaysOriginal)
    }
}
