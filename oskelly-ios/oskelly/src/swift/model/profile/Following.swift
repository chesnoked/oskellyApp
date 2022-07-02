//
//  Following.swift
//
//  Created by Виталий Хлудеев on 09.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Following: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let avatar = "avatar"
    static let nickname = "nickname"
    static let doIFollow = "doIFollow"
    static let id = "id"
    static let productsForSale = "productsForSale"
    static let subscribers = "subscribers"
    static let pro = "pro"
  }

  // MARK: Properties
  public var avatar: String?
  public var nickname: String?
  public var doIFollow: Bool? = false
  public var id: Int?
  public var productsForSale: Int?
  public var subscribers: Int?
  public var pro: Bool? = false

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
    avatar = json[SerializationKeys.avatar].string
    nickname = json[SerializationKeys.nickname].string
    doIFollow = json[SerializationKeys.doIFollow].boolValue
    id = json[SerializationKeys.id].int
    productsForSale = json[SerializationKeys.productsForSale].int
    subscribers = json[SerializationKeys.subscribers].int
    pro = json[SerializationKeys.pro].boolValue
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = avatar { dictionary[SerializationKeys.avatar] = value }
    if let value = nickname { dictionary[SerializationKeys.nickname] = value }
    dictionary[SerializationKeys.doIFollow] = doIFollow
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = productsForSale { dictionary[SerializationKeys.productsForSale] = value }
    if let value = subscribers { dictionary[SerializationKeys.subscribers] = value }
    dictionary[SerializationKeys.pro] = pro
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.avatar = aDecoder.decodeObject(forKey: SerializationKeys.avatar) as? String
    self.nickname = aDecoder.decodeObject(forKey: SerializationKeys.nickname) as? String
    self.doIFollow = aDecoder.decodeBool(forKey: SerializationKeys.doIFollow)
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.productsForSale = aDecoder.decodeObject(forKey: SerializationKeys.productsForSale) as? Int
    self.subscribers = aDecoder.decodeObject(forKey: SerializationKeys.subscribers) as? Int
    self.pro = aDecoder.decodeBool(forKey: SerializationKeys.pro)
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(avatar, forKey: SerializationKeys.avatar)
    aCoder.encode(nickname, forKey: SerializationKeys.nickname)
    aCoder.encode(doIFollow, forKey: SerializationKeys.doIFollow)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(productsForSale, forKey: SerializationKeys.productsForSale)
    aCoder.encode(subscribers, forKey: SerializationKeys.subscribers)
    aCoder.encode(pro, forKey: SerializationKeys.pro)
  }

}
