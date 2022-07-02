//
// Created by Виталий Хлудеев on 19.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class RegistrationView : RegistrationBaseView {

    let fullName = RegistrationInput()
    let confirmPassword = RegistrationInput()
    let phone = RegistrationInput()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        fieldsContainer.addSubview(fullName)
        fieldsContainer.addSubview(confirmPassword)
        fieldsContainer.addSubview(phone)
        contentView.addSubview(existsAccountButton)
        fullName.setup(imageName: "assets/images/security/Profile icon.png", placeHolder: "Ваш псевдоним", isSecure: false, needCapitalization: true)
        fullName.snp.makeConstraints { make in
            make.top.equalTo(fieldsContainer)
            make.left.equalTo(fieldsContainer)
            make.right.equalTo(fieldsContainer)
        }

        phone.setup(imageName: "assets/images/security/phone.png", placeHolder: "Контактный телефон", isSecure: false, needCapitalization: false, keyboardType: .phonePad)
        phone.snp.makeConstraints { make in
            make.top.equalTo(fullName.snp.bottom)
            make.left.equalTo(fieldsContainer)
            make.right.equalTo(fieldsContainer)
        }

        email.snp.makeConstraints { make in
            make.top.equalTo(phone.snp.bottom)
            make.left.equalTo(fieldsContainer)
            make.right.equalTo(fieldsContainer)
        }

        confirmPassword.setup(imageName: "assets/images/security/confirm icon.png", placeHolder: "Подтвердите пароль", isSecure: true, needCapitalization: false)
        confirmPassword.snp.makeConstraints { make in
            make.top.equalTo(password.snp.bottom)
            make.left.equalTo(fieldsContainer)
            make.right.equalTo(fieldsContainer)
        }

        continueButton.addTarget(self, action: #selector(self.continueButtonClicked(_:)), for: .touchUpInside)
        existsAccountButton.setTitle("Уже есть аккаунт", for: .normal)
        existsAccountButton.addTarget(self, action: #selector(self.existsAccountButtonClicked(_:)), for: .touchUpInside)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func existsAccountButtonClicked(_ sender: Any) {
        let vc = self.storyboard.instantiateViewController(withIdentifier: "AuthenticationViewController") as! AuthenticationViewController
        controller.present(vc, animated: true, completion: nil)
    }

    func continueButtonClicked(_ sender: Any) {

        let fullNameValue = fullName.textField.text!;
        let emailValue = email.textField.text!;
        let passwordValue = password.textField.text!;
        let confirmPasswordValue = confirmPassword.textField.text!;
        let phoneValue = phone.textField.text!;

        GlobalProvider.instance.getApiRequester().register(
                        fullName: fullNameValue,
                        email: emailValue,
                        password: passwordValue,
                        confirmPassword: confirmPasswordValue,
                        phone: phoneValue
                )
                .responseJSON { response in
                    if let status = response.response?.statusCode {
                        switch(status) {
                        case 200:
                            let hashedPasswordValue = GlobalProvider.instance.getPasswordEncoder().getHashedPassword(password: passwordValue, salt: emailValue)
                            GlobalProvider.instance.getAccountManager().setupCredentials(email: emailValue, password: hashedPasswordValue)
                            let vc = self.storyboard.instantiateViewController(withIdentifier: "ApplicationTabBarController") as! UITabBarController
                            self.controller.present(vc, animated: false, completion: nil)
                            GlobalProvider.instance.synchronizeData()

                            GlobalProvider.instance.getAccountManager().sendDeviceToken()
                        case 400:
                            let JSON = response.result.value as! NSDictionary
                            var errorMessage = "";
                            if(JSON.object(forKey: "nickname") != nil) {
                                errorMessage += JSON.object(forKey: "nickname")! as! String
                                errorMessage += "\n"
                            }
                            if(JSON.object(forKey: "email") != nil) {
                                errorMessage += JSON.object(forKey: "email")! as! String
                                errorMessage += "\n"
                            }
                            if(JSON.object(forKey: "password") != nil) {
                                errorMessage += JSON.object(forKey: "password")! as! String
                                errorMessage += "\n"
                            }

                            if(JSON.object(forKey: "confirmPassword") != nil) {
                                errorMessage += JSON.object(forKey: "confirmPassword")! as! String
                            }

                            let alert = UIAlertController(title: "Ошибка регистрации", message: errorMessage, preferredStyle: UIAlertControllerStyle.alert)
                            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
                            self.controller.present(alert, animated: true, completion: nil)
                        default:
                            let alert = UIAlertController(title: "Ошибка сервера", message: "Повторите попытку позже", preferredStyle: UIAlertControllerStyle.alert)
                            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
                            self.controller.present(alert, animated: true, completion: nil)
                        }
                    }
                }
    }
}