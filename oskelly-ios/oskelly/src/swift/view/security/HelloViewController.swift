//
//  HelloViewController.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 07.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class HelloViewController: UIViewController {
    
    let minWidth = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)
    
    let topView = UIView()
    
    let bottomView = UIView()
    
    let logoImageView = UIImageView(image: UIImage(named: "assets/images/security/HelloLogo.png"))
    
    let welcomeImageView = UIImageView(image: UIImage(named: "assets/images/security/WELCOME TO.png"))
    
    let continueButton = WhiteButton()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubview(logoImageView)
        view.addSubview(welcomeImageView)
        view.addSubview(topView)
        view.addSubview(bottomView)
        view.addSubview(continueButton)
        
        topView.snp.makeConstraints { make in
            make.width.equalTo(view)
            make.centerX.equalTo(view)
            make.top.equalTo(view)
            make.height.equalTo(view).dividedBy(3.2)
        }
        
        bottomView.snp.makeConstraints { make in
            make.bottom.equalTo(view)
            make.height.equalTo(view).dividedBy(5.5)
        }
        
        welcomeImageView.contentMode = .scaleAspectFit
        welcomeImageView.snp.makeConstraints { make in
            make.centerX.equalTo(view)
            make.top.equalTo(topView.snp.bottom)
        }
        
        logoImageView.contentMode = .scaleAspectFit
        logoImageView.snp.makeConstraints { make in
            make.centerX.equalTo(view)
            make.top.equalTo(welcomeImageView.snp.bottom).offset(30)
        }
        
        continueButton.setTitle("Продолжить", for: .normal)
        continueButton.snp.makeConstraints { make in
            make.bottom.equalTo(bottomView.snp.top)
            make.centerX.equalTo(view)
            make.width.equalTo(minWidth * 0.90)
        }
        
        continueButton.addTarget(self, action: #selector(HelloViewController.continueButtonClicked(_:)), for: .touchUpInside)
    }
    
    func continueButtonClicked(_ sender: AnyObject?) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
//        let vc = storyboard.instantiateViewController(withIdentifier: "RegistrationViewController") as! RegistrationViewController
        let vc = storyboard.instantiateViewController(withIdentifier: "MainPageNavigationController") as! UIViewController
        self.present(vc, animated: true, completion: nil)
    }
}
