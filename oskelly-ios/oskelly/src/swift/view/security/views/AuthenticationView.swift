//
// Created by Виталий Хлудеев on 19.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AuthenticationView : RegistrationBaseView {

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        email.snp.makeConstraints { make in
            make.top.equalTo(fieldsContainer).inset(48)
            make.left.equalTo(fieldsContainer)
            make.right.equalTo(fieldsContainer)
        }
        continueButton.addTarget(self, action: #selector(self.continueButtonClicked(_:)), for: .touchUpInside)
        existsAccountButton.setTitle("Регистрация", for: .normal)
        existsAccountButton.addTarget(self, action: #selector(self.existsAccountButtonClicked(_:)), for: .touchUpInside)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func existsAccountButtonClicked(_ sender: Any) {
        let vc = storyboard.instantiateViewController(withIdentifier: "RegistrationViewController") as! RegistrationViewController
        controller.present(vc, animated: true, completion: nil)
    }

    func continueButtonClicked(_ sender: Any) {
        let emailValue = email.textField.text!;
        let passwordValue = password.textField.text!;

        let hashedPasswordValue = GlobalProvider.instance.getPasswordEncoder().getHashedPassword(password: passwordValue, salt: emailValue)

        GlobalProvider.instance.getApiRequester().authenticate(email: emailValue, password: hashedPasswordValue)
                .responseJSON { response in
                    if let status = response.response?.statusCode {
                        switch(status) {
                        case 200:
                            GlobalProvider.instance.getAccountManager().setupCredentials(email: emailValue, password: hashedPasswordValue)
                            let vc = self.storyboard.instantiateViewController(withIdentifier: "ApplicationTabBarController") as! UITabBarController
                            self.controller.present(vc, animated: false, completion: nil)
                            GlobalProvider.instance.synchronizeData()

                            GlobalProvider.instance.getAccountManager().sendDeviceToken()
                        case 401:
                            let JSON = response.result.value as! NSDictionary
                            var errorMessage = "";
                            if(JSON.object(forKey: "errorMessage") != nil) {
                                errorMessage += JSON.object(forKey: "errorMessage")! as! String
                            }
                            let alert = UIAlertController(title: "Введен неверный email или пароль", message: errorMessage, preferredStyle: UIAlertControllerStyle.alert)
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