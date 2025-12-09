package bob.colbaskin.it_tech2025.onboarding.presentation

interface OnBoardingAction {
    data object OnboardingInProgress: OnBoardingAction
    data object OnboardingComplete: OnBoardingAction
}
