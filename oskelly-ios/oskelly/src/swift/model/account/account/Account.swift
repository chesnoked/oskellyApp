//
//  Account.swift
//
//  Created by Виталий Хлудеев on 13.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Account: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let seller = "seller"
    static let city = "city"
    static let sellerRequisite = "sellerRequisite"
    static let deliveryRequisite = "deliveryRequisite"
    static let descriptionValue = "description"
    static let lastName = "lastName"
    static let likesCount = "likesCount"
    static let nickname = "nickname"
    static let email = "email"
    static let orders = "orders"
    static let registrationTime = "registrationTime"
    static let birthDate = "birthDate"
    static let followingsCount = "followingsCount"
    static let id = "id"
    static let phone = "phone"
    static let avatar = "avatar"
    static let firstName = "firstName"
    static let publishedProductsCount = "publishedProductsCount"
    static let followersCount = "followersCount"
  }

  // MARK: Properties
  public var seller: Seller?
  public var city: String?
  public var sellerRequisite: AccountSellerRequisite?
  public var deliveryRequisite: DeliveryRequisite?
  public var descriptionValue: String?
  public var lastName: String?
  public var likesCount: Int?
  public var nickname: String?
  public var email: String?
  public var orders: [Order]! = []
  public var registrationTime: String?
  public var birthDate: String?
  public var followingsCount: Int?
  public var id: Int!
  public var phone: String?
  public var avatar: String?
  public var firstName: String?
  public var publishedProductsCount: Int?
  public var followersCount: Int?

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    seller = Seller(json: json[SerializationKeys.seller])
    city = json[SerializationKeys.city].string
    sellerRequisite = AccountSellerRequisite(json: json[SerializationKeys.sellerRequisite])
    deliveryRequisite = DeliveryRequisite(json: json[SerializationKeys.deliveryRequisite])
    descriptionValue = json[SerializationKeys.descriptionValue].string
    lastName = json[SerializationKeys.lastName].string
    likesCount = json[SerializationKeys.likesCount].int
    nickname = json[SerializationKeys.nickname].string
    email = json[SerializationKeys.email].string
    if let items = json[SerializationKeys.orders].array { orders = items.map { Order(json: $0) } }
    registrationTime = json[SerializationKeys.registrationTime].string
    birthDate = json[SerializationKeys.birthDate].string
    followingsCount = json[SerializationKeys.followingsCount].int
    id = json[SerializationKeys.id].int
    phone = json[SerializationKeys.phone].string
    avatar = json[SerializationKeys.avatar].string
    firstName = json[SerializationKeys.firstName].string
    publishedProductsCount = json[SerializationKeys.publishedProductsCount].int
    followersCount = json[SerializationKeys.followersCount].int
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = seller { dictionary[SerializationKeys.seller] = value.dictionaryRepresentation() }
    if let value = city { dictionary[SerializationKeys.city] = value }
    if let value = sellerRequisite { dictionary[SerializationKeys.sellerRequisite] = value.dictionaryRepresentation() }
    if let value = deliveryRequisite { dictionary[SerializationKeys.deliveryRequisite] = value.dictionaryRepresentation() }
    if let value = descriptionValue { dictionary[SerializationKeys.descriptionValue] = value }
    if let value = lastName { dictionary[SerializationKeys.lastName] = value }
    if let value = likesCount { dictionary[SerializationKeys.likesCount] = value }
    if let value = nickname { dictionary[SerializationKeys.nickname] = value }
    if let value = email { dictionary[SerializationKeys.email] = value }
    if let value = orders { dictionary[SerializationKeys.orders] = value.map { $0.dictionaryRepresentation() } }
    if let value = registrationTime { dictionary[SerializationKeys.registrationTime] = value }
    if let value = birthDate { dictionary[SerializationKeys.birthDate] = value }
    if let value = followingsCount { dictionary[SerializationKeys.followingsCount] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = phone { dictionary[SerializationKeys.phone] = value }
    if let value = avatar { dictionary[SerializationKeys.avatar] = value }
    if let value = firstName { dictionary[SerializationKeys.firstName] = value }
    if let value = publishedProductsCount { dictionary[SerializationKeys.publishedProductsCount] = value }
    if let value = followersCount { dictionary[SerializationKeys.followersCount] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.seller = aDecoder.decodeObject(forKey: SerializationKeys.seller) as? Seller
    self.city = aDecoder.decodeObject(forKey: SerializationKeys.city) as? String
    self.sellerRequisite = aDecoder.decodeObject(forKey: SerializationKeys.sellerRequisite) as? AccountSellerRequisite
    self.deliveryRequisite = aDecoder.decodeObject(forKey: SerializationKeys.deliveryRequisite) as? DeliveryRequisite
    self.descriptionValue = aDecoder.decodeObject(forKey: SerializationKeys.descriptionValue) as? String
    self.lastName = aDecoder.decodeObject(forKey: SerializationKeys.lastName) as? String
    self.likesCount = aDecoder.decodeObject(forKey: SerializationKeys.likesCount) as? Int
    self.nickname = aDecoder.decodeObject(forKey: SerializationKeys.nickname) as? String
    self.email = aDecoder.decodeObject(forKey: SerializationKeys.email) as? String
    self.orders = aDecoder.decodeObject(forKey: SerializationKeys.orders) as? [Order]
    self.registrationTime = aDecoder.decodeObject(forKey: SerializationKeys.registrationTime) as? String
    self.birthDate = aDecoder.decodeObject(forKey: SerializationKeys.birthDate) as? String
    self.followingsCount = aDecoder.decodeObject(forKey: SerializationKeys.followingsCount) as? Int
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.phone = aDecoder.decodeObject(forKey: SerializationKeys.phone) as? String
    self.avatar = aDecoder.decodeObject(forKey: SerializationKeys.avatar) as? String
    self.firstName = aDecoder.decodeObject(forKey: SerializationKeys.firstName) as? String
    self.publishedProductsCount = aDecoder.decodeObject(forKey: SerializationKeys.publishedProductsCount) as? Int
    self.followersCount = aDecoder.decodeObject(forKey: SerializationKeys.followersCount) as? Int
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(seller, forKey: SerializationKeys.seller)
    aCoder.encode(city, forKey: SerializationKeys.city)
    aCoder.encode(sellerRequisite, forKey: SerializationKeys.sellerRequisite)
    aCoder.encode(deliveryRequisite, forKey: SerializationKeys.deliveryRequisite)
    aCoder.encode(descriptionValue, forKey: SerializationKeys.descriptionValue)
    aCoder.encode(lastName, forKey: SerializationKeys.lastName)
    aCoder.encode(likesCount, forKey: SerializationKeys.likesCount)
    aCoder.encode(nickname, forKey: SerializationKeys.nickname)
    aCoder.encode(email, forKey: SerializationKeys.email)
    aCoder.encode(orders, forKey: SerializationKeys.orders)
    aCoder.encode(registrationTime, forKey: SerializationKeys.registrationTime)
    aCoder.encode(birthDate, forKey: SerializationKeys.birthDate)
    aCoder.encode(followingsCount, forKey: SerializationKeys.followingsCount)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(phone, forKey: SerializationKeys.phone)
    aCoder.encode(avatar, forKey: SerializationKeys.avatar)
    aCoder.encode(firstName, forKey: SerializationKeys.firstName)
    aCoder.encode(publishedProductsCount, forKey: SerializationKeys.publishedProductsCount)
    aCoder.encode(followersCount, forKey: SerializationKeys.followersCount)
  }

}
