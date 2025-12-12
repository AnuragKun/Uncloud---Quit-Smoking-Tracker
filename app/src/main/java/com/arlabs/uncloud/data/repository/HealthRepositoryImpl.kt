package com.arlabs.uncloud.data.repository

import com.arlabs.uncloud.domain.model.HealthMilestone
import com.arlabs.uncloud.domain.repository.HealthRepository
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor() : HealthRepository {
        override fun getMilestones(): List<HealthMilestone> {
                return listOf(
                        HealthMilestone(
                                1200,
                                "Heart Rate Normalizes",
                                "Your blood pressure and pulse drop to normal levels as the immediate stimulant effects of nicotine wear off."
                        ),
                        HealthMilestone(
                                7200,
                                "Vasodilation",
                                "Your peripheral circulation improves, meaning the tips of your fingers and toes start to warm up as blood vessels relax."
                        ),
                        HealthMilestone(
                                28800,
                                "Nicotine Crash",
                                "Nicotine levels in the blood fall by 93%. This causes cravings, but it proves your body is actively flushing out the toxin."
                        ),
                        HealthMilestone(
                                43200,
                                "Oxygen Boost",
                                "Carbon Monoxide levels in your blood return to normal, allowing red blood cells to carry full loads of oxygen again."
                        ),
                        HealthMilestone(
                                86400,
                                "Heart Attack Risk I",
                                "Your risk of heart attack begins to decrease. With more oxygen, your heart doesn't have to work as hard to pump blood."
                        ),
                        HealthMilestone(
                                129600,
                                "Cell Repair",
                                "Carbon Monoxide is completely eliminated. Your lungs start the repair process and your body focuses energy on rebuilding cells."
                        ),
                        HealthMilestone(
                                172800,
                                "Nerve Endings",
                                "Damaged nerve endings start to regrow. This is the biological reason why your senses are about to improve."
                        ),
                        HealthMilestone(
                                172800,
                                "Taste & Smell",
                                "Food tastes better and smells become sharper. You might notice subtle flavors you haven't experienced in years."
                        ),
                        HealthMilestone(
                                259200,
                                "Breathing Eases",
                                "Your bronchial tubes begin to relax and open up, making it easier to breathe and increasing lung capacity."
                        ),
                        HealthMilestone(
                                259200,
                                "Chemical Freedom",
                                "A huge milestone: Nicotine is completely depleted from your body. Withdrawal is now mental, not chemical."
                        ),
                        HealthMilestone(
                                345600,
                                "Energy Spike",
                                "With oxygen levels fully restored and no toxins dragging you down, you may experience sudden bursts of natural energy."
                        ),
                        HealthMilestone(
                                432000,
                                "The Hump",
                                "You have passed the peak of physical withdrawal. From here on, physical symptoms will gradually become less intense."
                        ),
                        HealthMilestone(
                                518400,
                                "Hydration",
                                "Your body's hydration levels are normalizing. Smoking dehydrates the skin; you are now retaining water better for cellular health."
                        ),
                        HealthMilestone(
                                604800,
                                "Sleep Quality",
                                "Your sleep patterns begin to stabilize. You start getting more restorative deep sleep without nicotine withdrawal waking you up."
                        ),
                        HealthMilestone(
                                864000,
                                "Blood Circulation",
                                "Blood circulation to your gums and teeth improves significantly, aiding healing and reducing the risk of gum disease."
                        ),
                        HealthMilestone(
                                1036800,
                                "Headaches Fade",
                                "The frequent headaches caused by nicotine withdrawal or oxygen deprivation begin to fade away completely."
                        ),
                        HealthMilestone(
                                1209600,
                                "Lung Function I",
                                "Your circulation and lung function have improved significantly. You can handle physical exertion with less wheezing."
                        ),
                        HealthMilestone(
                                1555200,
                                "Stamina",
                                "Walking, climbing stairs, and daily chores become less tiring as your heart and lungs work more efficiently together."
                        ),
                        HealthMilestone(
                                1814400,
                                "Withdrawal Over",
                                "The brain fog lifts. Your brain receptors are adjusting to life without nicotine, and concentration improves."
                        ),
                        HealthMilestone(
                                1814400,
                                "Exercise Tolerance",
                                "Running and intense exercise feel easier. Your body is oxygenating muscles much more effectively during workouts."
                        ),
                        HealthMilestone(
                                2419200,
                                "Skin Health",
                                "Premature aging slows down. Acne may clear up and redness reduces as blood flow to the skin improves."
                        ),
                        HealthMilestone(
                                3024000,
                                "Insulin Sensitivity",
                                "Your body's sensitivity to insulin improves, helping to stabilize blood sugar levels and reducing diabetes risk."
                        ),
                        HealthMilestone(
                                3628800,
                                "Oral Hygiene",
                                "Your mouth is healing. The risk of developing gum infections drops, and dental treatments become more effective."
                        ),
                        HealthMilestone(
                                4233600,
                                "Voice Clarity",
                                "Any hoarseness or the raspiness known as 'smoker’s voice' begins to clear up as your vocal cords heal."
                        ),
                        HealthMilestone(
                                5184000,
                                "Bone Density",
                                "The process of smoking-related bone loss stops, and your body begins to remineralize and strengthen your bones."
                        ),
                        HealthMilestone(
                                6480000,
                                "Complexion",
                                "The greyish cast or 'smoker's pallor' fades from your skin, replaced by a healthier, natural glow."
                        ),
                        HealthMilestone(
                                7776000,
                                "Fertility Boost",
                                "Fertility improves for both men and women. The quality of the uterine lining improves and sperm potency increases."
                        ),
                        HealthMilestone(
                                9072000,
                                "Circulation II",
                                "Blood flow to your extremities (hands and feet) is now fully restored to that of a non-smoker level."
                        ),
                        HealthMilestone(
                                10368000,
                                "Dopamine Reset",
                                "Your brain's dopamine receptors have returned to normal density. You no longer need nicotine to feel 'normal' happiness."
                        ),
                        HealthMilestone(
                                11664000,
                                "Hair Health",
                                "Hair follicles receive more oxygen and nutrients. You may notice your hair looks shinier and thicker than before."
                        ),
                        HealthMilestone(
                                12960000,
                                "Immune System",
                                "Your immune system is much stronger. You are significantly less likely to catch colds or the flu than when you smoked."
                        ),
                        HealthMilestone(
                                14256000,
                                "Stomach Health",
                                "The lining of your stomach heals, and the risk of developing peptic ulcers drops significantly."
                        ),
                        HealthMilestone(
                                15552000,
                                "Cilia Regrowth",
                                "Tiny hair-like structures called cilia have regrown in your lungs to push mucus out and clean your lungs effectively."
                        ),
                        HealthMilestone(
                                18144000,
                                "Lung Capacity",
                                "You have regained as much lung capacity as is physically possible for your age. Deep breaths feel full and satisfying."
                        ),
                        HealthMilestone(
                                20736000,
                                "Stress Mastery",
                                "You have likely faced several stressful life events without smoking, proving that you have built true mental resilience."
                        ),
                        HealthMilestone(
                                23328000,
                                "Respiratory Healing",
                                "Coughing, sinus congestion, and fatigue have decreased dramatically. Your lungs are cleaner and resistant to infection."
                        ),
                        HealthMilestone(
                                25920000,
                                "Whiter Teeth",
                                "Without the daily tar intake, new stains stop accumulating and your smile looks noticeably brighter."
                        ),
                        HealthMilestone(
                                28512000,
                                "Finger Stains",
                                "Those stubborn yellow nicotine stains on your fingers and nails have finally grown out completely."
                        ),
                        HealthMilestone(
                                31536000,
                                "Heart Health",
                                "A massive victory: Your risk of coronary heart disease is now exactly half that of a person who still smokes."
                        ),
                        HealthMilestone(
                                47304000,
                                "Bronchitis Risk",
                                "Your risk of chronic bronchitis and other respiratory infections is now approaching that of a non-smoker."
                        ),
                        HealthMilestone(
                                63072000,
                                "Stroke Risk",
                                "Your risk of having a stroke has dropped significantly as your blood vessels have healed and widened."
                        ),
                        HealthMilestone(
                                94608000,
                                "Cervical Cancer",
                                "For women, the risk of developing cervical cancer has dropped significantly compared to smokers."
                        ),
                        HealthMilestone(
                                126144000,
                                "Endometrial Cancer",
                                "The risk of cancer of the lining of the uterus falls to that of a woman who has never smoked."
                        ),
                        HealthMilestone(
                                157680000,
                                "Stroke Risk II",
                                "Your risk of stroke is now the same as someone who has never smoked in their life."
                        ),
                        HealthMilestone(
                                157680000,
                                "Cancer Shield",
                                "Your risk of developing cancers of the mouth, throat, esophagus, and bladder is cut in half."
                        ),
                        HealthMilestone(
                                315360000,
                                "Lung Cancer",
                                "Your risk of dying from lung cancer is about half that of a person who is still smoking."
                        ),
                        HealthMilestone(
                                315360000,
                                "Pancreas Cancer",
                                "The risk of cancer of the larynx and pancreas decreases noticeably."
                        ),
                        HealthMilestone(
                                473040000,
                                "Tooth Loss",
                                "Your risk of losing teeth due to gum disease or decay is now the same as a non-smoker."
                        ),
                        HealthMilestone(
                                473040000,
                                "Heart Disease",
                                "Your risk of coronary heart disease is now the same as if you had never smoked a cigarette."
                        ),
                        HealthMilestone(
                                630720000,
                                "Total Recovery",
                                "Your overall risk of death from smoking-related causes drops to the level of a person who has never smoked."
                        )
                )
        }
}