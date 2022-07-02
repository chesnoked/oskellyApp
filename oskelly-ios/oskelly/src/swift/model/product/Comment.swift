//
//  Comment.swift
//
//  Created by Виталий Хлудеев on 08.01.18
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Comment: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let avatar = "avatar"
    static let publishTime = "publishTime"
    static let publishZonedDateTime = "publishZonedDateTime"
    static let user = "user"
    static let id = "id"
    static let userId = "userId"
    static let text = "text"
  }

  // MARK: Properties
  public var avatar: String?
  public var publishTime: String?
  public var publishZonedDateTime: String?
  public var user: String?
  public var id: Int?
  public var userId: Int?
  public var text: String?

  init(){}

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
    publishTime = json[SerializationKeys.publishTime].string
    publishZonedDateTime = json[SerializationKeys.publishZonedDateTime].string
    user = json[SerializationKeys.user].string
    id = json[SerializationKeys.id].int
    userId = json[SerializationKeys.userId].int
    text = json[SerializationKeys.text].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = avatar { dictionary[SerializationKeys.avatar] = value }
    if let value = publishTime { dictionary[SerializationKeys.publishTime] = value }
    if let value = publishZonedDateTime { dictionary[SerializationKeys.publishZonedDateTime] = value }
    if let value = user { dictionary[SerializationKeys.user] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = userId { dictionary[SerializationKeys.userId] = value }
    if let value = text { dictionary[SerializationKeys.text] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.avatar = aDecoder.decodeObject(forKey: SerializationKeys.avatar) as? String
    self.publishTime = aDecoder.decodeObject(forKey: SerializationKeys.publishTime) as? String
    self.publishZonedDateTime = aDecoder.decodeObject(forKey: SerializationKeys.publishZonedDateTime) as? String
    self.user = aDecoder.decodeObject(forKey: SerializationKeys.user) as? String
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.userId = aDecoder.decodeObject(forKey: SerializationKeys.userId) as? Int
    self.text = aDecoder.decodeObject(forKey: SerializationKeys.text) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(avatar, forKey: SerializationKeys.avatar)
    aCoder.encode(publishTime, forKey: SerializationKeys.publishTime)
    aCoder.encode(publishZonedDateTime, forKey: SerializationKeys.publishZonedDateTime)
    aCoder.encode(user, forKey: SerializationKeys.user)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(userId, forKey: SerializationKeys.userId)
    aCoder.encode(text, forKey: SerializationKeys.text)
  }

}
