//
//  ViewController.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 27.04.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var continueButton: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()
        continueButton.backgroundColor = UIColor(red: 1, green: 1, blue: 1, alpha: 1)
        continueButton.layer.borderWidth = 1
        continueButton.layer.borderColor = UIColor.black.cgColor
        continueButton.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

