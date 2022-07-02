//
//  PublicProfile.swift
//
//  Created by Виталий Хлудеев on 09.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class PublicProfile: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let doIFollow = "doIFollow"
    static let nickname = "nickname"
    static let city = "city"
    static let likesCount = "likesCount"
    static let pro = "pro"
    static let followingsCount = "followingsCount"
    static let id = "id"
    static let myProfile = "myProfile"
    static let pointsCount = "pointsCount"
    static let avatar = "avatar"
    static let followers = "followers"
    static let followings = "followings"
    static let followersCount = "followersCount"
    static let productsForSale = "productsForSale"
    static let trusted = "trusted"
  }

  // MARK: Properties
  public var doIFollow: Bool? = false
  public var nickname: String?
  public var city: String?
  public var likesCount: Int?
  public var pro: Bool! = false
  public var followingsCount: Int?
  public var id: Int!
  public var myProfile: Bool? = false
  public var pointsCount: Int?
  public var avatar: String?
  public var followers: [Following]?
  public var followings: [Following]?
  public var followersCount: Int?
  public var productsForSale: Int?
  public var trusted: Bool? = false

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
    doIFollow = json[SerializationKeys.doIFollow].boolValue
    nickname = json[SerializationKeys.nickname].string
    city = json[SerializationKeys.city].string
    likesCount = json[SerializationKeys.likesCount].int
    pro = json[SerializationKeys.pro].boolValue
    followingsCount = json[SerializationKeys.followingsCount].int
    id = json[SerializationKeys.id].int
    myProfile = json[SerializationKeys.myProfile].boolValue
    pointsCount = json[SerializationKeys.pointsCount].int
    avatar = json[SerializationKeys.avatar].string
    if let items = json[SerializationKeys.followers].array { followers = items.map { Following(json: $0) } }
    if let items = json[SerializationKeys.followings].array { followings = items.map { Following(json: $0) } }
    followersCount = json[SerializationKeys.followersCount].int
    productsForSale = json[SerializationKeys.productsForSale].int
    trusted = json[SerializationKeys.trusted].boolValue
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    dictionary[SerializationKeys.doIFollow] = doIFollow
    if let value = nickname { dictionary[SerializationKeys.nickname] = value }
    if let value = city { dictionary[SerializationKeys.city] = value }
    if let value = likesCount { dictionary[SerializationKeys.likesCount] = value }
    dictionary[SerializationKeys.pro] = pro
    if let value = followingsCount { dictionary[SerializationKeys.followingsCount] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    dictionary[SerializationKeys.myProfile] = myProfile
    if let value = pointsCount { dictionary[SerializationKeys.pointsCount] = value }
    if let value = avatar { dictionary[SerializationKeys.avatar] = value }
    if let value = followers { dictionary[SerializationKeys.followers] = value }
    if let value = followings { dictionary[SerializationKeys.followings] = value.map { $0.dictionaryRepresentation() } }
    if let value = followersCount { dictionary[SerializationKeys.followersCount] = value }
    if let value = productsForSale { dictionary[SerializationKeys.productsForSale] = value }
    dictionary[SerializationKeys.trusted] = trusted
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.doIFollow = aDecoder.decodeBool(forKey: SerializationKeys.doIFollow)
    self.nickname = aDecoder.decodeObject(forKey: SerializationKeys.nickname) as? String
    self.city = aDecoder.decodeObject(forKey: SerializationKeys.city) as? String
    self.likesCount = aDecoder.decodeObject(forKey: SerializationKeys.likesCount) as? Int
    self.pro = aDecoder.decodeBool(forKey: SerializationKeys.pro)
    self.followingsCount = aDecoder.decodeObject(forKey: SerializationKeys.followingsCount) as? Int
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.myProfile = aDecoder.decodeBool(forKey: SerializationKeys.myProfile)
    self.pointsCount = aDecoder.decodeObject(forKey: SerializationKeys.pointsCount) as? Int
    self.avatar = aDecoder.decodeObject(forKey: SerializationKeys.avatar) as? String
    self.followers = aDecoder.decodeObject(forKey: SerializationKeys.followers) as? [Following]
    self.followings = aDecoder.decodeObject(forKey: SerializationKeys.followings) as? [Following]
    self.followersCount = aDecoder.decodeObject(forKey: SerializationKeys.followersCount) as? Int
    self.productsForSale = aDecoder.decodeObject(forKey: SerializationKeys.productsForSale) as? Int
    self.trusted = aDecoder.decodeBool(forKey: SerializationKeys.trusted)
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(doIFollow, forKey: SerializationKeys.doIFollow)
    aCoder.encode(nickname, forKey: SerializationKeys.nickname)
    aCoder.encode(city, forKey: SerializationKeys.city)
    aCoder.encode(likesCount, forKey: SerializationKeys.likesCount)
    aCoder.encode(pro, forKey: SerializationKeys.pro)
    aCoder.encode(followingsCount, forKey: SerializationKeys.followingsCount)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(myProfile, forKey: SerializationKeys.myProfile)
    aCoder.encode(pointsCount, forKey: SerializationKeys.pointsCount)
    aCoder.encode(avatar, forKey: SerializationKeys.avatar)
    aCoder.encode(followers, forKey: SerializationKeys.followers)
    aCoder.encode(followings, forKey: SerializationKeys.followings)
    aCoder.encode(followersCount, forKey: SerializationKeys.followersCount)
    aCoder.encode(productsForSale, forKey: SerializationKeys.productsForSale)
    aCoder.encode(trusted, forKey: SerializationKeys.trusted)
  }

}
