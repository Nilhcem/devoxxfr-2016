package com.nilhcem.devoxxfr.scraper.model

import com.nilhcem.devoxxfr.scraper.model.devoxx.ScheduleDaySlot
import com.nilhcem.devoxxfr.scraper.model.output.Room
import java.text.SimpleDateFormat
import java.util.*
import com.nilhcem.devoxxfr.scraper.model.devoxx.Speaker as DevoxxSpeaker
import com.nilhcem.devoxxfr.scraper.model.output.Session as AppSession
import com.nilhcem.devoxxfr.scraper.model.output.Speaker as AppSpeaker

object Mapper {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")

    fun convertSpeaker(id: Int, speaker: DevoxxSpeaker): AppSpeaker {
        val name = "${speaker.firstName} ${speaker.lastName}".trim()
        val company = speaker.company?.trim()
        val bio = speaker.bio.clean()
        val blog = speaker.blog?.trim()
        val twitter = speaker.twitter?.trim()?.replace("@", "")

        val avatarURL = speaker.avatarURL?.trim()
        val picture = if (avatarURL == null || avatarURL.length == 0 || avatarURL.startsWith("data:image")) null else avatarURL

        return AppSpeaker(id + 1, speaker.uuid, name, company, bio, blog, twitter, picture)
    }

    fun convertSession(id: Int, slot: ScheduleDaySlot, speakersMap: Map<String, Int>): AppSession {
        val startAt = DATE_FORMAT.format(Date(slot.fromTimeMillis))
        val duration = ((slot.toTimeMillis - slot.fromTimeMillis) / 60000).toInt()
        val roomId = Room.getRoomId(slot.roomId)
        val title = if (slot.talk != null) slot.talk.title else slot.breakData?.nameFR
        val description = slot.talk?.summary?.clean()

        val speakersId = if (slot.talk == null) null else slot.talk.speakers
                .map { it.link.href }
                .map { it.substring(it.lastIndexOf("/") + 1) }
                .map { speakersMap[it] }
                .filterNotNull()

        return AppSession(id, startAt, duration, roomId, speakersId, title, description)
    }

    fun String.clean() = trim().replace("\r", "")
}
