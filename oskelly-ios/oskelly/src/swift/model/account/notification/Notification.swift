//
//  Notification.swift
//
//  Created by Виталий Хлудеев on 12.01.18
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Notification: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let prettyCreateTime = "prettyCreateTime"
    static let initiatorNickname = "initiatorNickname"
    static let urlOfTargetObject = "urlOfTargetObject"
    static let imageOfTargetObject = "imageOfTargetObject"
    static let id = "id"
    static let baseMessage = "baseMessage"
    static let initiatorId = "initiatorId"
    static let targetUserNickname = "targetUserNickname"
    static let readTime = "readTime"
    static let targetUserId = "targetUserId"
    static let createTime = "createTime"
    static let initiatorAvatar = "initiatorAvatar"
    static let fullMessage = "fullMessage"
  }

  // MARK: Properties
  public var prettyCreateTime: String?
  public var initiatorNickname: String?
  public var urlOfTargetObject: String?
  public var imageOfTargetObject: String?
  public var id: Int?
  public var baseMessage: String?
  public var initiatorId: Int?
  public var targetUserNickname: String?
  public var readTime: String?
  public var targetUserId: Int?
  public var createTime: String?
  public var initiatorAvatar: String?
  public var fullMessage: String?

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
    prettyCreateTime = json[SerializationKeys.prettyCreateTime].string
    initiatorNickname = json[SerializationKeys.initiatorNickname].string
    urlOfTargetObject = json[SerializationKeys.urlOfTargetObject].string
    imageOfTargetObject = json[SerializationKeys.imageOfTargetObject].string
    id = json[SerializationKeys.id].int
    baseMessage = json[SerializationKeys.baseMessage].string
    initiatorId = json[SerializationKeys.initiatorId].int
    targetUserNickname = json[SerializationKeys.targetUserNickname].string
    readTime = json[SerializationKeys.readTime].string
    targetUserId = json[SerializationKeys.targetUserId].int
    createTime = json[SerializationKeys.createTime].string
    initiatorAvatar = json[SerializationKeys.initiatorAvatar].string
    fullMessage = json[SerializationKeys.fullMessage].string
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = prettyCreateTime { dictionary[SerializationKeys.prettyCreateTime] = value }
    if let value = initiatorNickname { dictionary[SerializationKeys.initiatorNickname] = value }
    if let value = urlOfTargetObject { dictionary[SerializationKeys.urlOfTargetObject] = value }
    if let value = imageOfTargetObject { dictionary[SerializationKeys.imageOfTargetObject] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = baseMessage { dictionary[SerializationKeys.baseMessage] = value }
    if let value = initiatorId { dictionary[SerializationKeys.initiatorId] = value }
    if let value = targetUserNickname { dictionary[SerializationKeys.targetUserNickname] = value }
    if let value = readTime { dictionary[SerializationKeys.readTime] = value }
    if let value = targetUserId { dictionary[SerializationKeys.targetUserId] = value }
    if let value = createTime { dictionary[SerializationKeys.createTime] = value }
    if let value = initiatorAvatar { dictionary[SerializationKeys.initiatorAvatar] = value }
    if let value = fullMessage { dictionary[SerializationKeys.fullMessage] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.prettyCreateTime = aDecoder.decodeObject(forKey: SerializationKeys.prettyCreateTime) as? String
    self.initiatorNickname = aDecoder.decodeObject(forKey: SerializationKeys.initiatorNickname) as? String
    self.urlOfTargetObject = aDecoder.decodeObject(forKey: SerializationKeys.urlOfTargetObject) as? String
    self.imageOfTargetObject = aDecoder.decodeObject(forKey: SerializationKeys.imageOfTargetObject) as? String
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.baseMessage = aDecoder.decodeObject(forKey: SerializationKeys.baseMessage) as? String
    self.initiatorId = aDecoder.decodeObject(forKey: SerializationKeys.initiatorId) as? Int
    self.targetUserNickname = aDecoder.decodeObject(forKey: SerializationKeys.targetUserNickname) as? String
    self.readTime = aDecoder.decodeObject(forKey: SerializationKeys.readTime) as? String
    self.targetUserId = aDecoder.decodeObject(forKey: SerializationKeys.targetUserId) as? Int
    self.createTime = aDecoder.decodeObject(forKey: SerializationKeys.createTime) as? String
    self.initiatorAvatar = aDecoder.decodeObject(forKey: SerializationKeys.initiatorAvatar) as? String
    self.fullMessage = aDecoder.decodeObject(forKey: SerializationKeys.fullMessage) as? String
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(prettyCreateTime, forKey: SerializationKeys.prettyCreateTime)
    aCoder.encode(initiatorNickname, forKey: SerializationKeys.initiatorNickname)
    aCoder.encode(urlOfTargetObject, forKey: SerializationKeys.urlOfTargetObject)
    aCoder.encode(imageOfTargetObject, forKey: SerializationKeys.imageOfTargetObject)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(baseMessage, forKey: SerializationKeys.baseMessage)
    aCoder.encode(initiatorId, forKey: SerializationKeys.initiatorId)
    aCoder.encode(targetUserNickname, forKey: SerializationKeys.targetUserNickname)
    aCoder.encode(readTime, forKey: SerializationKeys.readTime)
    aCoder.encode(targetUserId, forKey: SerializationKeys.targetUserId)
    aCoder.encode(createTime, forKey: SerializationKeys.createTime)
    aCoder.encode(initiatorAvatar, forKey: SerializationKeys.initiatorAvatar)
    aCoder.encode(fullMessage, forKey: SerializationKeys.fullMessage)
  }

}
