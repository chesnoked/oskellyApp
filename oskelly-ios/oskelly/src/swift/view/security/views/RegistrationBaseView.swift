//
// Created by Виталий Хлудеев on 19.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class RegistrationBaseView : UITableViewCell {

    let storyboard = UIStoryboard(name: "Main", bundle: nil)

    let minWidth = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)

    let topView: UIView = UIView()
    let middleView: UIView = UIView()
    let logo: UIImageView = UIImageView(image: UIImage(named: "assets/images/security/logo.png"))
    let fieldsContainer = UIView()

    let email: RegistrationInput = RegistrationInput()
    let password: RegistrationInput = RegistrationInput()

    let continueButton = DarkButton()
    let existsAccountButton = WhiteButton()

    let middleView2: UIView = UIView() // работает как сепаратор

    var controller: UIViewController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(logo)
        contentView.addSubview(topView)
        contentView.addSubview(middleView)
        contentView.addSubview(fieldsContainer)
        contentView.addSubview(middleView2)
        contentView.addSubview(existsAccountButton)
        fieldsContainer.addSubview(email)
        fieldsContainer.addSubview(password)
        contentView.addSubview(continueButton)
        topView.snp.makeConstraints { make in
            make.top.equalTo(contentView)
            make.height.equalTo(contentView).dividedBy(6.5)
        }
        logo.snp.makeConstraints { make in
            make.top.equalTo(topView.snp.bottom)
            make.centerX.equalTo(contentView)
        }
        middleView.snp.makeConstraints { make in
            make.top.equalTo(logo.snp.bottom)
            make.width.equalTo(contentView)
            make.height.equalTo(contentView).dividedBy(11.5)
        }
        fieldsContainer.snp.makeConstraints { make in
            make.top.equalTo(middleView.snp.bottom)
            make.left.equalTo(contentView)
            make.right.equalTo(contentView)
            make.height.equalTo(48 * 5)
        }
        middleView2.snp.makeConstraints { make in
            make.top.equalTo(fieldsContainer.snp.bottom)
            make.width.equalTo(contentView)
            make.height.equalTo(contentView).dividedBy(15.5)
        }
        continueButton.snp.makeConstraints { make in
            make.top.equalTo(middleView2.snp.bottom)
            make.centerX.equalTo(contentView)
            make.width.equalTo(minWidth * 0.9)
        }
        existsAccountButton.snp.makeConstraints { make in
            make.top.equalTo(continueButton.snp.bottom).offset(10)
            make.centerX.equalTo(continueButton)
            make.width.equalTo(continueButton)
        }
        password.snp.makeConstraints { make in
            make.top.equalTo(email.snp.bottom)
            make.left.equalTo(fieldsContainer)
            make.right.equalTo(fieldsContainer)
        }
        email.setup(imageName: "assets/images/security/mail icon.png", placeHolder: "Email", isSecure: false, needCapitalization: false)
        password.setup(imageName: "assets/images/security/lock icon.png", placeHolder: "Пароль", isSecure: true, needCapitalization: false)
        continueButton.setTitle("Продолжить", for: .normal)
    }

    func render(controller: UIViewController) {
        self.controller = controller
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}